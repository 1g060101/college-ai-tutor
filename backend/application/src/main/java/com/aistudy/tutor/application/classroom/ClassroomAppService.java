package com.aistudy.tutor.application.classroom;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.classroom.model.ParsedMaterial;
import com.aistudy.tutor.domain.classroom.model.Summary;
import com.aistudy.tutor.domain.classroom.model.Transcript;
import com.aistudy.tutor.domain.classroom.repository.ParsedMaterialRepository;
import com.aistudy.tutor.domain.classroom.repository.SummaryRepository;
import com.aistudy.tutor.domain.classroom.repository.TranscriptRepository;
import com.aistudy.tutor.infrastructure.aigateway.AiCallFacade;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 课堂辅助应用服务：课件解析/要点提炼、录音转写重点提炼、片段重讲、过期录音清理。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ClassroomAppService {

    /** 送入 LLM 的内容长度上限（避免超长 token） */
    private static final int MAX_AI_INPUT_LENGTH = 4000;
    /** 二进制判定阈值：不可读字符占比超过该值视为非纯文本文件 */
    private static final int BINARY_RATIO_PERCENT = 20;

    private final ParsedMaterialRepository parsedMaterialRepository;
    private final TranscriptRepository transcriptRepository;
    private final SummaryRepository summaryRepository;
    private final AiCallFacade aiCallFacade;

    /**
     * 上传课件/PPT/教材：按文件名后缀判定 fileType；纯文本内容可直接解析为 DONE，
     * 否则记为 FAILED；解析成功时调用 AI 生成 300 字要点提炼并写 summary。
     */
    @Transactional
    public Map<String, Object> uploadMaterial(Long userId, String fileName, byte[] bytes, Long courseId) {
        String fileType = detectFileType(fileName);
        String text = readPlainText(bytes);
        if (text == null || text.isBlank()) {
            ParsedMaterial failed = parsedMaterialRepository.save(
                    new ParsedMaterial(userId, courseId, fileName, fileType, null, "FAILED", "暂不支持该格式解析，请粘贴纯文本"));
            return materialResult(failed, null);
        }
        ParsedMaterial material = parsedMaterialRepository.save(
                new ParsedMaterial(userId, courseId, fileName, fileType, text, "DONE", null));
        String summary = generateMaterialSummary(userId, fileName, text);
        summaryRepository.save(new Summary(userId, "MATERIAL", material.getId(), summary));
        return materialResult(material, summary);
    }

    /**
     * 录音转文字（演示环境无 ASR，直接接收用户粘贴的转写文本）+ 重点提炼；
     * 转写记录 30 天后过期，提炼结果写 summary(source_type=TRANSCRIPT)。
     */
    @Transactional
    public Map<String, Object> transcribe(Long userId, Long courseId, String lectureName, String transcriptText) {
        if (transcriptText == null || transcriptText.isBlank()) {
            throw new BusinessException(400, "转写文本不能为空");
        }
        Transcript transcript = transcriptRepository.save(
                new Transcript(userId, courseId, lectureName, transcriptText, LocalDateTime.now().plusDays(30)));
        String summary = generateTranscriptSummary(userId, lectureName, transcriptText);
        summaryRepository.save(new Summary(userId, "TRANSCRIPT", transcript.getId(), summary));
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", transcript.getId());
        result.put("lectureName", transcript.getLectureName());
        result.put("summary", summary);
        return result;
    }

    /**
     * 重讲某片段：校验转写记录归属后，定位包含关键词的句子（含前后共最多 3 句）作为片段，
     * 交由 AI 针对该片段口语化重新讲解。
     * 说明：内部会经 AiCallFacade 写入审计日志，需普通读写事务（readOnly 会导致回滚-only）。
     */
    @Transactional
    public Map<String, Object> replay(Long userId, Long transcriptId, String keyword) {
        Transcript transcript = transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new BusinessException(404, "录音记录不存在"));
        if (!transcript.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该录音记录");
        }
        String fragment = extractFragment(transcript.getContent(), keyword);
        String explanation = explainFragment(userId, fragment);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("fragment", fragment);
        result.put("explanation", explanation);
        return result;
    }

    /**
     * 删除过期录音：软删 transcript（deleted=true）。
     */
    @Transactional
    public void deleteTranscript(Long userId, Long transcriptId) {
        Transcript transcript = transcriptRepository.findById(transcriptId)
                .orElseThrow(() -> new BusinessException(404, "录音记录不存在"));
        if (!transcript.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权访问该录音记录");
        }
        transcript.softDelete();
        transcriptRepository.save(transcript);
    }

    /**
     * 按文件名后缀判定文件类型：.ppt/.pptx→PPT，.pdf→PDF，.doc/.docx→WORD，其他→MD
     */
    private String detectFileType(String fileName) {
        if (fileName == null) {
            return "MD";
        }
        String lower = fileName.toLowerCase();
        if (lower.endsWith(".ppt") || lower.endsWith(".pptx")) {
            return "PPT";
        }
        if (lower.endsWith(".pdf")) {
            return "PDF";
        }
        if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
            return "WORD";
        }
        return "MD";
    }

    /**
     * 尝试按 UTF-8 读取纯文本：内容为空或含大量不可读二进制字符时返回 null（判定解析失败）。
     */
    private String readPlainText(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        String text = new String(bytes, StandardCharsets.UTF_8);
        int total = text.length();
        if (total == 0) {
            return null;
        }
        int bad = 0;
        for (int i = 0; i < total; i++) {
            char c = text.charAt(i);
            if (c == '\uFFFD' || (c < 32 && c != '\n' && c != '\r' && c != '\t')) {
                bad++;
            }
        }
        // 不可读字符占比超过阈值 → 视为二进制文件
        if (bad * 100 / total > BINARY_RATIO_PERCENT) {
            return null;
        }
        return text;
    }

    /**
     * 课件要点提炼：300 字以内中文总结（AI 不可用时降级为占位文本，保证入库流程可用）
     */
    private String generateMaterialSummary(Long userId, String fileName, String content) {
        String system = "你是一名大学助教，请对下面这份课件进行 300 字以内的要点提炼，分点列出核心概念与结论，使用中文。";
        String input = "课件名称：" + (fileName == null || fileName.isBlank() ? "未命名" : fileName)
                + "\n课件内容：\n" + truncate(content);
        return safeAiSummary(userId, system, input);
    }

    /**
     * 录音转写重点提炼：300 字以内中文总结（AI 不可用时降级为占位文本）
     */
    private String generateTranscriptSummary(Long userId, String lectureName, String content) {
        String system = "你是一名大学助教，请根据下面这段课堂录音转写文本，提炼出本堂课的重点内容，输出 300 字以内的中文总结，分点列出。";
        String input = "课堂名称：" + (lectureName == null || lectureName.isBlank() ? "未命名" : lectureName)
                + "\n转写文本：\n" + truncate(content);
        return safeAiSummary(userId, system, input);
    }

    /**
     * 统一的 AI 总结调用：调用失败时降级返回占位文本，不影响课件/转写入库
     */
    private String safeAiSummary(Long userId, String system, String input) {
        try {
            AiCallResult result = aiCallFacade.chat(userId, system,
                    List.of(Map.of("role", "user", "content", input)), 0.3);
            if (result.content() != null && !result.content().isBlank()) {
                return result.content();
            }
        } catch (Exception e) {
            log.warn("AI 要点提炼失败，降级返回占位总结: {}", e.getMessage());
        }
        return "（演示环境未生成 AI 总结，请配置 API Key 后重试）";
    }

    /**
     * 片段口语化重讲（AI 不可用时降级为占位文本）
     */
    private String explainFragment(Long userId, String fragment) {
        String system = "你是一名大学助教，请用口语化、通俗易懂的中文重新讲解下面这段课堂内容片段，逻辑清晰，可适当补充例子帮助理解。";
        try {
            AiCallResult result = aiCallFacade.chat(userId, system,
                    List.of(Map.of("role", "user", "content", "课堂片段：\n" + fragment)), 0.7);
            if (result.content() != null && !result.content().isBlank()) {
                return result.content();
            }
        } catch (Exception e) {
            log.warn("片段重讲失败，降级返回占位讲解: {}", e.getMessage());
        }
        return "（演示环境未生成重讲内容，请配置 API Key 后重试）";
    }

    /**
     * 定位片段：按中文句号/问号/叹号/换行切分句子，取含关键词句子的前后共最多 3 句
     */
    private String extractFragment(String content, String keyword) {
        if (content == null || content.isBlank()) {
            throw new BusinessException(400, "录音内容为空，无法定位片段");
        }
        if (keyword == null || keyword.isBlank()) {
            throw new BusinessException(400, "关键词不能为空");
        }
        List<String> sentences = new ArrayList<>();
        for (String s : content.split("[。！？!?；;\\n]+")) {
            if (!s.isBlank()) {
                sentences.add(s.trim());
            }
        }
        int idx = -1;
        for (int i = 0; i < sentences.size(); i++) {
            if (sentences.get(i).contains(keyword)) {
                idx = i;
                break;
            }
        }
        if (idx < 0) {
            throw new BusinessException(404, "未在录音中找到包含该关键词的内容");
        }
        int from = Math.max(0, idx - 1);
        int to = Math.min(sentences.size(), idx + 2);
        return String.join("。", sentences.subList(from, to));
    }

    /**
     * 截断超长内容，避免 AI 调用超 token
     */
    private String truncate(String content) {
        if (content == null) {
            return "";
        }
        return content.length() <= MAX_AI_INPUT_LENGTH ? content : content.substring(0, MAX_AI_INPUT_LENGTH);
    }

    private Map<String, Object> materialResult(ParsedMaterial material, String summary) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", material.getId());
        result.put("fileName", material.getFileName());
        result.put("fileType", material.getFileType());
        result.put("status", material.getStatus());
        result.put("errorMsg", material.getErrorMsg());
        result.put("summary", summary);
        return result;
    }
}
