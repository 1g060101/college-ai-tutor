package com.aistudy.tutor.application.teacher;

import com.aistudy.tutor.domain.avatar.model.AvatarConfig;
import com.aistudy.tutor.domain.avatar.repository.AvatarConfigRepository;
import com.aistudy.tutor.domain.classroom.model.AlertLog;
import com.aistudy.tutor.domain.classroom.model.Assignment;
import com.aistudy.tutor.domain.classroom.model.Roster;
import com.aistudy.tutor.domain.classroom.model.SchoolClass;
import com.aistudy.tutor.domain.classroom.model.WarnRule;
import com.aistudy.tutor.domain.classroom.repository.AlertLogRepository;
import com.aistudy.tutor.domain.classroom.repository.AssignmentRepository;
import com.aistudy.tutor.domain.classroom.repository.RosterRepository;
import com.aistudy.tutor.domain.classroom.repository.SchoolClassRepository;
import com.aistudy.tutor.domain.classroom.repository.WarnRuleRepository;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.report.model.UserKnowledgePoint;
import com.aistudy.tutor.domain.report.repository.UserKnowledgePointRepository;
import com.aistudy.tutor.domain.user.model.User;
import com.aistudy.tutor.domain.user.repository.UserRepository;
import com.aistudy.tutor.shared.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 教师/管理后台应用服务：知识库维护 / 作业发布 / 班级管理（加入、成员、预警）/ 数字人风格配置
 */
@Service
@RequiredArgsConstructor
public class TeacherAppService {

    /** 掌握度预警默认阈值 */
    private static final BigDecimal DEFAULT_MASTERY_THRESHOLD = new BigDecimal("60");

    private final SchoolClassRepository schoolClassRepository;
    private final RosterRepository rosterRepository;
    private final AssignmentRepository assignmentRepository;
    private final WarnRuleRepository warnRuleRepository;
    private final AlertLogRepository alertLogRepository;
    private final UserRepository userRepository;
    private final UserKnowledgePointRepository userKnowledgePointRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final AvatarConfigRepository avatarConfigRepository;

    /** 班级成员条目 */
    public record StudentItem(Long studentId, String nickname) {}

    /** 班级成员名单结果 */
    public record RosterData(Long classId, String className, List<StudentItem> students) {}

    /** 预警规则条目 */
    public record RuleItem(String ruleType, BigDecimal threshold) {}

    /** 薄弱知识点条目 */
    public record WeakPointItem(String name, BigDecimal masteryScore) {}

    /** 预警学生条目 */
    public record RiskStudentItem(Long studentId, String nickname, List<WeakPointItem> weakPoints, int alertCount) {}

    /** 预警名单结果 */
    public record RiskData(Long classId, List<RuleItem> rules, List<RiskStudentItem> risks) {}

    /**
     * 知识库维护：新增知识点
     */
    @Transactional
    public Long createKnowledgePoint(Long courseId, String name, String tags, Integer difficulty,
                                     BigDecimal weight, String description) {
        KnowledgePoint kp = new KnowledgePoint(courseId, name, tags,
                difficulty == null ? 3 : difficulty, weight == null ? BigDecimal.ONE : weight, description);
        return knowledgePointRepository.save(kp).getId();
    }

    /**
     * 作业发布：校验班级归属（class.teacherId == 当前教师）后创建作业
     */
    @Transactional
    public Assignment publishAssignment(Long teacherId, Long classId, String title, String description, LocalDateTime dueDate) {
        requireOwnClass(teacherId, classId);
        return assignmentRepository.save(new Assignment(classId, teacherId, title, description, dueDate));
    }

    /**
     * 创建班级并生成随机邀请码
     */
    @Transactional
    public SchoolClass createClass(Long teacherId, String name, String subject) {
        String inviteCode = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return schoolClassRepository.save(new SchoolClass(teacherId, name, subject, inviteCode));
    }

    /**
     * 学生加入班级：按邀请码匹配，花名册已存在则幂等返回（唯一键冲突忽略）
     */
    @Transactional
    public SchoolClass joinClass(Long studentId, Long classId, String inviteCode) {
        SchoolClass schoolClass = schoolClassRepository.findByInviteCode(inviteCode)
                .orElseThrow(() -> new BusinessException(400, "邀请码错误"));
        if (!schoolClass.getId().equals(classId)) {
            throw new BusinessException(400, "邀请码错误");
        }
        if (!rosterRepository.existsByClassIdAndStudentId(classId, studentId)) {
            rosterRepository.save(new Roster(classId, studentId));
        }
        return schoolClass;
    }

    /**
     * 班级成员名单（含学生昵称）
     */
    @Transactional(readOnly = true)
    public RosterData listRoster(Long teacherId, Long classId) {
        SchoolClass schoolClass = requireOwnClass(teacherId, classId);
        List<StudentItem> students = rosterRepository.findByClassId(classId).stream()
                .map(r -> new StudentItem(r.getStudentId(), nicknameOf(r.getStudentId())))
                .toList();
        return new RosterData(classId, schoolClass.getName(), students);
    }

    /**
     * 预警名单：掌握度低于阈值（warn_rule MASTERY 规则，无则默认 60）的学生列入预警；
     * 防误报：同一学生同一天已生成过预警则不再重复写日志（直接复用，仍返回在列表中）
     */
    @Transactional
    public RiskData riskList(Long teacherId, Long classId) {
        SchoolClass schoolClass = requireOwnClass(teacherId, classId);
        List<WarnRule> rules = warnRuleRepository.findByTeacherId(teacherId);
        // 掌握度阈值：取 MASTERY 规则，无则默认 60
        List<WarnRule> masteryRules = rules.stream()
                .filter(r -> "MASTERY".equals(r.getRuleType()) && r.getThreshold() != null)
                .toList();
        BigDecimal threshold = masteryRules.isEmpty() ? DEFAULT_MASTERY_THRESHOLD : masteryRules.get(0).getThreshold();
        // 规则输出：未配置任何规则时展示生效中的默认规则
        List<RuleItem> ruleItems;
        if (rules.isEmpty()) {
            ruleItems = List.of(new RuleItem("MASTERY", DEFAULT_MASTERY_THRESHOLD));
        } else {
            ruleItems = new ArrayList<>(rules.stream()
                    .map(r -> new RuleItem(r.getRuleType(), r.getThreshold()))
                    .toList());
            if (masteryRules.isEmpty()) {
                ruleItems.add(new RuleItem("MASTERY", DEFAULT_MASTERY_THRESHOLD));
            }
        }
        List<RiskStudentItem> risks = new ArrayList<>();
        for (Roster roster : rosterRepository.findByClassId(classId)) {
            Long studentId = roster.getStudentId();
            List<WeakPointItem> weakPoints = new ArrayList<>();
            for (UserKnowledgePoint ukp : userKnowledgePointRepository.findByUserId(studentId)) {
                if (ukp.getMasteryScore() != null && ukp.getMasteryScore().compareTo(threshold) < 0) {
                    String kpName = knowledgePointRepository.findById(ukp.getId().getKnowledgePointId())
                            .map(KnowledgePoint::getName).orElse("未知知识点");
                    weakPoints.add(new WeakPointItem(kpName, ukp.getMasteryScore()));
                }
            }
            if (weakPoints.isEmpty()) {
                continue;
            }
            String nickname = nicknameOf(studentId);
            long alertCount = alertLogRepository.countByTeacherIdAndClassIdAndStudentId(teacherId, classId, studentId);
            // 防误报：同一天已生成过预警则不再重复写日志
            LocalDateTime todayStart = LocalDate.now().atStartOfDay();
            LocalDateTime todayEnd = todayStart.plusDays(1);
            if (!alertLogRepository.existsByTeacherIdAndClassIdAndStudentIdAndCreatedAtBetween(
                    teacherId, classId, studentId, todayStart, todayEnd)) {
                Long ruleId = masteryRules.isEmpty() ? null : masteryRules.get(0).getId();
                String message = "学生【" + nickname + "】存在薄弱知识点："
                        + weakPoints.stream().map(WeakPointItem::name).collect(Collectors.joining("、"));
                alertLogRepository.save(new AlertLog(teacherId, classId, studentId, ruleId, message));
                alertCount++;
            }
            risks.add(new RiskStudentItem(studentId, nickname, weakPoints, (int) alertCount));
        }
        return new RiskData(classId, ruleItems, risks);
    }

    /**
     * 自定义数字人教学风格：按 user_id=当前教师 upsert avatar_config（无则新建，有则更新 tone/style）
     */
    @Transactional
    public void updateClassStyle(Long teacherId, Long classId, String tone, String style) {
        requireOwnClass(teacherId, classId);
        AvatarConfig config = avatarConfigRepository.findByUserId(teacherId)
                .orElseGet(() -> new AvatarConfig(teacherId, "小灵", tone, style, null));
        config.update(null, tone, style, null);
        avatarConfigRepository.save(config);
    }

    /**
     * 设置班级预警规则：按 teacher_id+rule_type upsert（阈值可配，防误报）
     */
    @Transactional
    public void saveWarnRule(Long teacherId, Long classId, String ruleType, BigDecimal threshold) {
        requireOwnClass(teacherId, classId);
        String type = (ruleType == null || ruleType.isBlank()) ? "MASTERY" : ruleType.trim().toUpperCase();
        WarnRule rule = warnRuleRepository.findActiveByRuleType(teacherId, type).orElse(null);
        if (rule == null) {
            warnRuleRepository.save(new WarnRule(teacherId, type, threshold, "自定义预警阈值"));
        } else {
            rule.updateThreshold(threshold);
            warnRuleRepository.save(rule);
        }
    }

    /**
     * 班级归属校验：班级不存在 404，非本人班级 403
     */
    private SchoolClass requireOwnClass(Long teacherId, Long classId) {
        SchoolClass schoolClass = schoolClassRepository.findById(classId)
                .orElseThrow(() -> new BusinessException(404, "班级不存在"));
        if (!schoolClass.getTeacherId().equals(teacherId)) {
            throw new BusinessException(403, "无权访问该班级");
        }
        return schoolClass;
    }

    private String nicknameOf(Long userId) {
        return userRepository.findById(userId).map(User::getNickname).orElse("未知用户");
    }
}
