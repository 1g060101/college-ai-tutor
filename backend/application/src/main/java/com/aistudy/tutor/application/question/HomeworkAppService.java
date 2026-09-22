package com.aistudy.tutor.application.question;

import com.aistudy.tutor.application.report.MasteryRecalcService;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.question.model.ErrorBook;
import com.aistudy.tutor.domain.question.model.Question;
import com.aistudy.tutor.domain.question.model.ReminderConfig;
import com.aistudy.tutor.domain.question.repository.ErrorBookRepository;
import com.aistudy.tutor.domain.question.repository.QuestionRepository;
import com.aistudy.tutor.domain.question.repository.ReminderConfigRepository;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.exception.BusinessException;
import com.aistudy.tutor.shared.result.PageResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 作业与习题辅导应用服务：作业创建 / 作答提交（错题本自动收录）/ 错题本分页 / 提醒配置。
 */
@Service
@RequiredArgsConstructor
public class HomeworkAppService {

    /** 新建错题记录的默认错误原因 */
    private static final String DEFAULT_ERROR_REASON = "作答错误，已自动收录";

    private final QuestionRepository questionRepository;
    private final ErrorBookRepository errorBookRepository;
    private final ReminderConfigRepository reminderConfigRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final MasteryRecalcService masteryRecalcService;

    /**
     * 新建作业题目的入参（由接口层 CreateAssignmentRequest.QuestionInput 转换而来）
     */
    public record QuestionInput(String content, String type, String answer, String analysis,
                                Integer difficulty, Long knowledgePointId) {}

    /**
     * 批量创建作业题目：difficulty 未提供时按题目长度启发式估算，source 标记为「作业」。
     * 返回创建成功的题目 id 列表。
     */
    @Transactional
    public List<Long> createAssignments(Long courseId, List<QuestionInput> questions) {
        if (questions == null || questions.isEmpty()) {
            throw new BusinessException(400, "题目列表不能为空");
        }
        List<Long> ids = new ArrayList<>();
        for (QuestionInput q : questions) {
            int difficulty = q.difficulty() != null ? q.difficulty() : estimateDifficulty(q.content());
            Question question = new Question(courseId, q.knowledgePointId(),
                    q.type() == null || q.type().isBlank() ? "SUBJECTIVE" : q.type(),
                    q.content(), q.answer(), q.analysis(), difficulty, "作业");
            ids.add(questionRepository.save(question).getId());
        }
        return ids;
    }

    /**
     * 提交作业作答：幂等记录作答事件（requestId）并触发掌握度重算；
     * 答错时按 user_id+question_id upsert 错题本（已存在则错误次数 +1、刷新时间、重置掌握状态）。
     */
    @Transactional
    public String submitAnswer(Long userId, Long questionId, String answerContent, boolean correct,
                               BigDecimal score, Integer durationSeconds, String requestId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new BusinessException(404, "题目不存在"));
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }
        final String reqId = requestId;
        masteryRecalcService.recordEventWithQuestion(userId, questionId, question.getCourseId(),
                question.getKnowledgePointId(), correct, score, "HOMEWORK", reqId);
        if (!correct) {
            ErrorBook errorBook = errorBookRepository.findByUserIdAndQuestionId(userId, questionId)
                    .orElseGet(() -> new ErrorBook(userId, questionId, question.getCourseId(),
                            question.getKnowledgePointId(), reqId, answerContent, DEFAULT_ERROR_REASON));
            if (errorBook.getId() != null) {
                // 已存在：错误次数 +1、刷新最近错误时间、重置掌握状态
                errorBook.markWrongAgain();
            }
            errorBookRepository.save(errorBook);
            return "作答已记录，已加入错题本";
        }
        return "作答已记录";
    }

    /**
     * 错题本分页查询（可按知识点过滤，只查当前用户未删除记录），附带题目内容与知识点名称。
     */
    @Transactional(readOnly = true)
    public PageResult<Map<String, Object>> pageErrorBook(Long userId, Long knowledgePointId, PageQuery query) {
        PageResult<ErrorBook> page = errorBookRepository.pageByUserId(userId, knowledgePointId, query);
        List<Map<String, Object>> list = page.getList().stream().map(this::toErrorBookMap).toList();
        return new PageResult<>(list, page.getTotal(), page.getPageNum(), page.getPageSize(), page.getTotalPages());
    }

    /**
     * 保存截止/完成度提醒配置：按 user_id upsert（每用户一行）。
     */
    @Transactional
    public void saveReminderConfig(Long userId, boolean enabled, int remindBeforeHours) {
        ReminderConfig config = reminderConfigRepository.findByUserId(userId).orElse(null);
        if (config == null) {
            config = new ReminderConfig(userId, enabled, remindBeforeHours);
        } else {
            config.update(enabled, remindBeforeHours);
        }
        reminderConfigRepository.save(config);
    }

    private Map<String, Object> toErrorBookMap(ErrorBook eb) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", eb.getId());
        m.put("questionId", eb.getQuestionId());
        m.put("content", questionRepository.findById(eb.getQuestionId()).map(Question::getContent).orElse(null));
        m.put("knowledgePointId", eb.getKnowledgePointId());
        m.put("kpName", eb.getKnowledgePointId() != null
                ? knowledgePointRepository.findById(eb.getKnowledgePointId()).map(KnowledgePoint::getName).orElse(null)
                : null);
        m.put("wrongAnswer", eb.getWrongAnswer());
        m.put("errorReason", eb.getErrorReason());
        m.put("errorCount", eb.getErrorCount());
        m.put("mastered", eb.isMastered());
        m.put("lastWrongAt", eb.getLastWrongAt());
        return m;
    }

    /**
     * 难度启发式估算：题目长度 &lt;50 字符=2，&lt;150=3，否则=4
     */
    private int estimateDifficulty(String content) {
        int len = content == null ? 0 : content.length();
        if (len < 50) {
            return 2;
        }
        if (len < 150) {
            return 3;
        }
        return 4;
    }
}
