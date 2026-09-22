package com.aistudy.tutor.application.report;

import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.question.repository.AnswerEventRepository;
import com.aistudy.tutor.domain.report.model.Diagnosis;
import com.aistudy.tutor.domain.report.model.StudyMinutesAgg;
import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.repository.DiagnosisRepository;
import com.aistudy.tutor.domain.report.repository.StudyMinutesAggRepository;
import com.aistudy.tutor.domain.report.repository.UserKnowledgePointRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 学习报告应用服务：汇总 / 雷达 / 诊断 / 周期报告。
 */
@Service
@RequiredArgsConstructor
public class ReportAppService {

    /** 固定建议（现场生成诊断时使用） */
    private static final List<String> SUGGESTIONS = List.of("复习薄弱知识点", "每天练习同类题 10 道", "向 AI 答疑提问巩固");

    private final UserKnowledgePointRepository userKnowledgePointRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final StudyMinutesAggRepository studyMinutesAggRepository;
    private final DiagnosisRepository diagnosisRepository;
    private final AnswerEventRepository answerEventRepository;
    private final ObjectMapper objectMapper;

    /**
     * 近 7 天学习汇总：时长、作答数、正确率、按日趋势。
     */
    @Transactional(readOnly = true)
    public Map<String, Object> summary(Long userId) {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(6);
        LocalDateTime fromTime = LocalDateTime.now().minusDays(7);

        List<StudyMinutesAgg> aggs = studyMinutesAggRepository.findByUserIdAndStatDateBetween(userId, from, today);
        int studyMinutes = 0;
        int focusMinutes = 0;
        Map<LocalDate, Integer> byDate = new LinkedHashMap<>();
        for (StudyMinutesAgg agg : aggs) {
            studyMinutes += agg.getStudyMinutes();
            focusMinutes += agg.getFocusMinutes();
            byDate.merge(agg.getStatDate(), agg.getStudyMinutes(), Integer::sum);
        }

        long answeredCount = answerEventRepository.countByUserIdAndCreatedAtAfter(userId, fromTime);
        long correctCount = answerEventRepository.countByUserIdAndCreatedAtAfterAndCorrectTrue(userId, fromTime);
        double correctRate = answeredCount == 0 ? 0 : Math.round(correctCount * 100.0 / answeredCount) / 100.0;

        // 近 7 天按日期补全趋势，无记录填 0
        List<Map<String, Object>> trend = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            LocalDate date = from.plusDays(i);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("date", date.toString());
            item.put("studyMinutes", byDate.getOrDefault(date, 0));
            trend.add(item);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("studyMinutes", studyMinutes);
        result.put("focusMinutes", focusMinutes);
        result.put("answeredCount", answeredCount);
        result.put("correctRate", correctRate);
        result.put("trend", trend);
        return result;
    }

    /**
     * 掌握度雷达：各知识点掌握情况。
     */
    @Transactional(readOnly = true)
    public Map<String, Object> radar(Long userId) {
        List<Map<String, Object>> dimensions = new ArrayList<>();
        for (UserKnowledgePoint ukp : userKnowledgePointRepository.findByUserId(userId)) {
            String name = knowledgePointRepository.findById(ukp.getId().getKnowledgePointId())
                    .map(KnowledgePoint::getName)
                    .orElse(null);
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("knowledgePointId", ukp.getId().getKnowledgePointId());
            item.put("name", name);
            item.put("masteryScore", ukp.getMasteryScore());
            item.put("attemptCount", ukp.getAttemptCount());
            dimensions.add(item);
        }
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("dimensions", dimensions);
        return result;
    }

    /**
     * 学习诊断：优先取最新 WEEK 诊断记录，无记录则现场生成。
     */
    @Transactional(readOnly = true)
    public Map<String, Object> diagnosis(Long userId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Diagnosis diagnosis = diagnosisRepository
                .findTopByUserIdAndPeriodOrderByCreatedAtDesc(userId, "WEEK").orElse(null);
        if (diagnosis != null) {
            result.put("period", diagnosis.getPeriod());
            result.put("summary", diagnosis.getSummary());
            result.put("weakPoints", parseJsonList(diagnosis.getWeakPoints()));
            result.put("suggestions", parseJsonList(diagnosis.getSuggestions()));
            return result;
        }
        // 现场生成：掌握度低于 60 的知识点作为薄弱点
        LocalDate today = LocalDate.now();
        result.put("period", "WEEK");
        result.put("summary", "近7天学习 " + sumStudyMinutes(userId, today.minusDays(6), today) + " 分钟");
        result.put("weakPoints", buildWeakPoints(userId));
        result.put("suggestions", new ArrayList<>(SUGGESTIONS));
        return result;
    }

    /**
     * 周期学习报告：week = 近 7 天，month = 近 30 天。
     */
    @Transactional(readOnly = true)
    public Map<String, Object> report(Long userId, String period) {
        int days = switch (period) {
            case "week" -> 7;
            case "month" -> 30;
            default -> throw new BusinessException(400, "period 仅支持 week/month");
        };
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(days - 1L);
        LocalDateTime fromTime = LocalDateTime.now().minusDays(days);

        long answeredCount = answerEventRepository.countByUserIdAndCreatedAtAfter(userId, fromTime);
        long correctCount = answerEventRepository.countByUserIdAndCreatedAtAfterAndCorrectTrue(userId, fromTime);
        double correctRate = answeredCount == 0 ? 0 : Math.round(correctCount * 100.0 / answeredCount) / 100.0;

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("period", period);
        result.put("studyMinutes", sumStudyMinutes(userId, from, today));
        result.put("focusMinutes", sumFocusMinutes(userId, from, today));
        result.put("answeredCount", answeredCount);
        result.put("correctRate", correctRate);
        result.put("weakPoints", buildWeakPoints(userId));
        result.put("suggestions", new ArrayList<>(SUGGESTIONS));
        return result;
    }

    private int sumStudyMinutes(Long userId, LocalDate from, LocalDate to) {
        int total = 0;
        for (StudyMinutesAgg agg : studyMinutesAggRepository.findByUserIdAndStatDateBetween(userId, from, to)) {
            total += agg.getStudyMinutes();
        }
        return total;
    }

    private int sumFocusMinutes(Long userId, LocalDate from, LocalDate to) {
        int total = 0;
        for (StudyMinutesAgg agg : studyMinutesAggRepository.findByUserIdAndStatDateBetween(userId, from, to)) {
            total += agg.getFocusMinutes();
        }
        return total;
    }

    /**
     * 掌握度低于 60 的知识点作为薄弱点（带固定原因）。
     */
    private List<Map<String, Object>> buildWeakPoints(Long userId) {
        List<Map<String, Object>> weakPoints = new ArrayList<>();
        for (UserKnowledgePoint ukp : userKnowledgePointRepository.findByUserId(userId)) {
            BigDecimal mastery = ukp.getMasteryScore();
            if (mastery != null && mastery.compareTo(BigDecimal.valueOf(60)) < 0) {
                Long kpId = ukp.getId().getKnowledgePointId();
                String name = knowledgePointRepository.findById(kpId).map(KnowledgePoint::getName).orElse(null);
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("knowledgePointId", kpId);
                item.put("name", name);
                item.put("masteryScore", mastery);
                item.put("reason", "掌握度低于 60，建议针对性练习");
                weakPoints.add(item);
            }
        }
        return weakPoints;
    }

    /**
     * JSON 字符串解析为列表，解析失败返回 null。
     */
    private List<Object> parseJsonList(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Object>>() {});
        } catch (Exception e) {
            return null;
        }
    }
}
