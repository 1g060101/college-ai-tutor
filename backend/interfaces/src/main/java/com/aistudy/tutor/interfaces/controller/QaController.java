package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.qa.QaAppService;
import com.aistudy.tutor.domain.ai.OcrGateway;
import com.aistudy.tutor.domain.qa.model.QaSession;
import com.aistudy.tutor.domain.qa.model.QaTurn;
import com.aistudy.tutor.domain.qa.model.ReplyMode;
import com.aistudy.tutor.interfaces.dto.qa.AskRequest;
import com.aistudy.tutor.interfaces.dto.qa.CreateSessionRequest;
import com.aistudy.tutor.interfaces.dto.qa.FeedbackRequest;
import com.aistudy.tutor.interfaces.dto.qa.QaSessionResponse;
import com.aistudy.tutor.interfaces.dto.qa.TurnResponse;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

/**
 * AI 智能答疑接口：创建会话 / SSE 流式提问 / 拍照提问 / 轮次查询 / 反馈
 */
@RestController
@RequestMapping("/api/v1/qa")
@RequiredArgsConstructor
public class QaController {

    /** 图片大小上限 5MB */
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;

    private final QaAppService qaAppService;
    private final OcrGateway ocrGateway;

    /**
     * 创建答疑会话
     */
    @PostMapping("/sessions")
    public Result<QaSessionResponse> createSession(@Valid @RequestBody CreateSessionRequest request) {
        Long userId = SecurityUtils.currentUserId();
        // replyMode 可空（默认 GUIDED），显式传 null 时兜底
        ReplyMode replyMode = (request.getReplyMode() == null || request.getReplyMode().isBlank())
                ? ReplyMode.GUIDED : ReplyMode.valueOf(request.getReplyMode());
        QaSession session = qaAppService.createSession(userId, request.getCourseId(), request.getTitle(), replyMode);
        return Result.success(toSessionResponse(session));
    }

    /**
     * SSE 流式提问。先做前置校验（异常在 SSE 建立前抛出，可被全局异常处理器正常返回 JSON）
     */
    @PostMapping(value = "/sessions/{id}/ask", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter ask(@PathVariable Long id, @Valid @RequestBody AskRequest request) {
        Long userId = SecurityUtils.currentUserId();
        qaAppService.validateSessionAccess(userId, id);
        SseEmitter emitter = new SseEmitter(30000L);
        qaAppService.askStream(userId, id, request.getQuestion(), emitter);
        return emitter;
    }

    /**
     * 拍照提问：优先使用手动输入的题目，否则 OCR 识别图片；
     * 识别成功后创建会话并同步完成一次答疑
     */
    @PostMapping("/upload-image")
    public Result<TurnResponse> uploadImage(@RequestParam("file") MultipartFile file,
                                            @RequestParam(value = "question", required = false) String question) throws IOException {
        Long userId = SecurityUtils.currentUserId();
        if (file == null || file.isEmpty()) {
            return Result.fail(400, "图片不能为空");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            return Result.fail(400, "图片大小不能超过 5MB");
        }
        String text = question;
        if (text == null || text.isBlank()) {
            text = ocrGateway.recognize(Base64.getEncoder().encodeToString(file.getBytes()));
            if (text == null || text.isBlank()) {
                return Result.fail(400, "图片文字识别失败，请手动输入题目");
            }
        }
        String title = text.length() > 20 ? text.substring(0, 20) : text;
        QaSession session = qaAppService.createSession(userId, null, title, ReplyMode.GUIDED);
        QaTurn turn = qaAppService.askSync(userId, session.getId(), text);
        return Result.success(toTurnResponse(turn));
    }

    /**
     * 查询会话全部轮次
     */
    @GetMapping("/sessions/{id}/turns")
    public Result<List<TurnResponse>> turns(@PathVariable Long id) {
        Long userId = SecurityUtils.currentUserId();
        List<QaTurn> turns = qaAppService.listTurns(userId, id);
        return Result.success(turns.stream().map(this::toTurnResponse).toList());
    }

    /**
     * 提交单轮反馈
     */
    @PostMapping("/sessions/{id}/feedback")
    public Result<String> feedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequest request) {
        Long userId = SecurityUtils.currentUserId();
        qaAppService.feedback(userId, id, request.getTurnId(), request.getFeedback(),
                request.isHelpful(), request.isCorrect());
        return Result.success("反馈已记录");
    }

    private QaSessionResponse toSessionResponse(QaSession session) {
        return new QaSessionResponse(session.getId(), session.getTitle(), session.getCourseId(),
                session.getStatus(), session.getReplyMode(), session.getCreatedAt());
    }

    private TurnResponse toTurnResponse(QaTurn turn) {
        return new TurnResponse(turn.getId(), turn.getSessionId(), turn.getQuestion(), turn.getAnswer(),
                turn.getKpHit(), turn.getGuidanceLevel(), turn.getFeedback(), turn.getCreatedAt());
    }
}
