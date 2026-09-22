package com.aistudy.tutor.application.paper;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.paper.model.HotKpRank;
import com.aistudy.tutor.domain.paper.model.Paper;
import com.aistudy.tutor.domain.paper.model.PaperQuestion;
import com.aistudy.tutor.domain.paper.repository.HotKpRankRepository;
import com.aistudy.tutor.domain.paper.repository.PaperQuestionRepository;
import com.aistudy.tutor.domain.paper.repository.PaperRepository;
import com.aistudy.tutor.domain.question.model.Question;
import com.aistudy.tutor.domain.question.repository.QuestionRepository;
import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.repository.UserKnowledgePointRepository;
import com.aistudy.tutor.infrastructure.aigateway.AiCallFacade;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * 备考冲刺应用服务：试卷上传与 AI 整卷讲解 / 高频考点串讲 / 答题技巧个性化建议
 */
@Service
@RequiredArgsConstructor
public class PaperAppService {

    /** 掌握度薄弱阈值（<60 视为薄弱知识点） */
    private static final BigDecimal WEAK_MASTERY = new BigDecimal("60");

    /** 高频考点兜底数量 */
    private static final int HOT_POINT_LIMIT = 5;

    private final PaperRepository paperRepository;
    private final PaperQuestionRepository paperQuestionRepository;
    private final HotKpRankRepository hotKpRankRepository;
    private final QuestionRepository questionRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final UserKnowledgePointRepository userKnowledgePointRepository;
    private final AiCallFacade aiCallFacade;

    /** 试卷题目输入（由接口层 DTO 转换而来，避免 application 依赖 interfaces） */
    public record PaperQuestionItem(Long questionId, BigDecimal score) {}

    /** 整卷讲解结果 */
    public record PaperAnalysisData(Long paperId, String title, String analysisText) {}

    /** 高频考点条目 */
    public record HotPointItem(Long knowledgePointId, String name, Integer examFreq, BigDecimal weight) {}

    /** 高频考点串讲结果 */
    public record HotPointsData(Long courseId, List<HotPointItem> points, String narrationText) {}

    /** 答题技巧建议结果 */
    public record TipsData(List<String> tips) {}

    /**
     * 上传卷面：创建试卷并逐条写入题目关联（sort_order=题号）
     */
    @Transactional
    public Paper createPaper(Long courseId, String title, String paperType, Integer year,
                             Integer durationMinutes, List<PaperQuestionItem> questions) {
        Paper paper = paperRepository.save(new Paper(courseId, title, paperType, year, durationMinutes));
        int sortOrder = 1;
        for (PaperQuestionItem item : questions) {
            paperQuestionRepository.save(new PaperQuestion(paper.getId(), item.questionId(), sortOrder++, item.score()));
        }
        return paper;
    }

    /**
     * 整卷讲解：用 PaperQuestion 关联查 Question 内容拼接题面，交由 AI 逐题讲解
     * 说明：内部会经 AiCallFacade 写入审计日志，需普通读写事务（readOnly 会导致回滚-only）。
     */
    @Transactional
    public PaperAnalysisData analyzePaper(Long userId, Long paperId) {
        Paper paper = paperRepository.findById(paperId)
                .orElseThrow(() -> new BusinessException(404, "试卷不存在"));
        List<PaperQuestion> paperQuestions = paperQuestionRepository.findByPaperId(paperId);
        StringBuilder sb = new StringBuilder();
        for (PaperQuestion pq : paperQuestions) {
            Question question = questionRepository.findById(pq.getQuestionId()).orElse(null);
            if (question == null || question.getContent() == null) {
                continue;
            }
            sb.append("第").append(pq.getSortOrder()).append("题（")
              .append(pq.getScore()).append("分）：").append(question.getContent()).append("\n");
        }
        String analysisText;
        if (sb.isEmpty()) {
            analysisText = "该试卷暂无可讲解的题目内容";
        } else {
            String system = "你是大学助教，请逐题讲解以下试卷的解题思路与涉及考点，语言使用中文，条理清晰。";
            AiCallResult result = aiCallFacade.chat(userId, system,
                    List.of(Map.of("role", "user", "content", "请逐题讲解以下试卷：\n" + sb)), 0.7);
            analysisText = result.content() == null ? "AI 暂未生成讲解，请稍后重试" : result.content();
        }
        return new PaperAnalysisData(paper.getId(), paper.getTitle(), analysisText);
    }

    /**
     * 高频考点串讲：优先取 hot_kp_rank 最近 rank_date 的记录，无数据则按知识点权重降序取前 5 兜底；
     * 对 top1 知识点生成 AI 串讲文本
     */
    @Transactional
    public HotPointsData hotPoints(Long userId, Long courseId) {
        List<HotPointItem> points = new ArrayList<>();
        // 1) 命中高频排行：最近一次 rank_date 的记录
        if (courseId != null) {
            List<HotKpRank> ranks = hotKpRankRepository.findByCourseId(courseId);
            if (!ranks.isEmpty()) {
                LocalDate latest = ranks.get(0).getRankDate();
                for (HotKpRank rank : ranks) {
                    if (!latest.equals(rank.getRankDate())) {
                        break; // 按日期倒序，遇到更早的日期即停止
                    }
                    KnowledgePoint kp = knowledgePointRepository.findById(rank.getKnowledgePointId()).orElse(null);
                    if (kp == null) {
                        continue;
                    }
                    points.add(new HotPointItem(kp.getId(), kp.getName(), rank.getExamFreq(), kp.getWeight()));
                }
            }
        }
        // 2) 兜底：知识点按 weight 降序取前 5
        if (points.isEmpty()) {
            List<KnowledgePoint> kps = knowledgePointRepository.findAllActive().stream()
                    .filter(kp -> courseId == null || courseId.equals(kp.getCourseId()))
                    .sorted(Comparator.comparing(KnowledgePoint::getWeight, Comparator.nullsLast(Comparator.reverseOrder())))
                    .limit(HOT_POINT_LIMIT)
                    .toList();
            for (KnowledgePoint kp : kps) {
                points.add(new HotPointItem(kp.getId(), kp.getName(), 0, kp.getWeight()));
            }
        }
        // 3) 对 top1 知识点生成 AI 串讲文本
        String narrationText;
        if (points.isEmpty()) {
            narrationText = "暂无高频考点数据";
        } else {
            HotPointItem top = points.get(0);
            String system = "你是大学助教，请串讲该高频考点，说明考查方式与备考要点，语言使用中文。";
            AiCallResult result = aiCallFacade.chat(userId, system,
                    List.of(Map.of("role", "user", "content", "请串讲高频考点「" + top.name() + "」")), 0.7);
            narrationText = result.content() == null ? "AI 暂未生成串讲内容，请稍后重试" : result.content();
        }
        return new HotPointsData(courseId, points, narrationText);
    }

    /**
     * 答题技巧/时间分配（按薄弱点个性化）：掌握度<60 的知识点为薄弱点，无数据回退通用模板
     */
    @Transactional
    public TipsData examTips(Long userId) {
        List<String> weakNames = new ArrayList<>();
        for (UserKnowledgePoint ukp : userKnowledgePointRepository.findByUserId(userId)) {
            if (ukp.getMasteryScore() != null && ukp.getMasteryScore().compareTo(WEAK_MASTERY) < 0) {
                knowledgePointRepository.findById(ukp.getId().getKnowledgePointId())
                        .ifPresent(kp -> weakNames.add(kp.getName()));
            }
        }
        List<String> tips;
        if (weakNames.isEmpty()) {
            // 无掌握度数据时回退通用模板
            tips = List.of(
                    "先做有把握的题目，稳定基础分，再攻克难题",
                    "选择题善用排除法与代入验证，控制单题用时",
                    "大题先写关键公式与推导步骤，按点得分",
                    "预留最后 10 分钟检查答题卡与易错计算"
            );
        } else {
            String system = "你是大学助教，请根据学生薄弱知识点给出答题技巧与时间分配建议，语言使用中文，200字以内。";
            AiCallResult result = aiCallFacade.chat(userId, system,
                    List.of(Map.of("role", "user", "content", "我的薄弱知识点是：" + String.join("、", weakNames))), 0.7);
            String content = result.content();
            tips = (content == null || content.isBlank())
                    ? List.of("建议优先复习薄弱知识点并配合专项练习")
                    : Arrays.stream(content.split("\\n")).map(String::trim).filter(s -> !s.isBlank()).toList();
        }
        return new TipsData(tips);
    }
}
