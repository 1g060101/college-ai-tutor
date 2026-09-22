package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.teacher.TeacherAppService;
import com.aistudy.tutor.domain.classroom.model.Assignment;
import com.aistudy.tutor.domain.classroom.model.SchoolClass;
import com.aistudy.tutor.domain.user.model.UserRole;
import com.aistudy.tutor.interfaces.dto.teacher.ClassResponse;
import com.aistudy.tutor.interfaces.dto.teacher.ClassStyleRequest;
import com.aistudy.tutor.interfaces.dto.teacher.CreateClassRequest;
import com.aistudy.tutor.interfaces.dto.teacher.JoinClassRequest;
import com.aistudy.tutor.interfaces.dto.teacher.KnowledgePointRequest;
import com.aistudy.tutor.interfaces.dto.teacher.PublishAssignmentRequest;
import com.aistudy.tutor.interfaces.dto.teacher.PublishAssignmentResponse;
import com.aistudy.tutor.interfaces.dto.teacher.RiskResponse;
import com.aistudy.tutor.interfaces.dto.teacher.RosterResponse;
import com.aistudy.tutor.interfaces.dto.teacher.WarnRuleRequest;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 教师/管理后台接口：知识库维护 / 作业发布 / 班级管理（创建、加入、成员、预警、风格）
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TeacherController {

    private final TeacherAppService teacherAppService;

    /**
     * 知识库维护：新增知识点
     */
    @PostMapping("/teacher/knowledge")
    public Result<Long> createKnowledgePoint(@Valid @RequestBody KnowledgePointRequest request) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long id = teacherAppService.createKnowledgePoint(request.getCourseId(), request.getName(),
                request.getTags(), request.getDifficulty(), request.getWeight(), request.getDescription());
        return Result.success(id);
    }

    /**
     * 创建班级（生成邀请码）
     */
    @PostMapping("/teacher/classes")
    public Result<ClassResponse> createClass(@Valid @RequestBody CreateClassRequest request) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long teacherId = SecurityUtils.currentUserId();
        SchoolClass schoolClass = teacherAppService.createClass(teacherId, request.getName(), request.getSubject());
        return Result.success(toClassResponse(schoolClass));
    }

    /**
     * 作业发布（校验班级归属）
     */
    @PostMapping("/teacher/assignments")
    public Result<PublishAssignmentResponse> publishAssignment(@Valid @RequestBody PublishAssignmentRequest request) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long teacherId = SecurityUtils.currentUserId();
        Assignment assignment = teacherAppService.publishAssignment(teacherId, request.getClassId(),
                request.getTitle(), request.getDescription(), request.getDueDate());
        return Result.success(new PublishAssignmentResponse(assignment.getId()));
    }

    /**
     * 学生加入班级（凭邀请码，学生/教师均可用，不要求教师角色）
     */
    @PostMapping("/classes/{id}/join")
    public Result<ClassResponse> join(@PathVariable Long id, @Valid @RequestBody JoinClassRequest request) {
        Long studentId = SecurityUtils.currentUserId();
        SchoolClass schoolClass = teacherAppService.joinClass(studentId, id, request.getInviteCode());
        return Result.success(toClassResponse(schoolClass));
    }

    /**
     * 班级成员名单
     */
    @GetMapping("/classes/{id}/roster")
    public Result<RosterResponse> roster(@PathVariable Long id) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long teacherId = SecurityUtils.currentUserId();
        TeacherAppService.RosterData data = teacherAppService.listRoster(teacherId, id);
        List<RosterResponse.StudentItem> students = data.students().stream()
                .map(s -> new RosterResponse.StudentItem(s.studentId(), s.nickname()))
                .toList();
        return Result.success(new RosterResponse(data.classId(), data.className(), students));
    }

    /**
     * 预警名单（阈值可配，防误报）
     */
    @GetMapping("/classes/{id}/risk")
    public Result<RiskResponse> risk(@PathVariable Long id) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long teacherId = SecurityUtils.currentUserId();
        TeacherAppService.RiskData data = teacherAppService.riskList(teacherId, id);
        List<RiskResponse.RuleItem> rules = data.rules().stream()
                .map(r -> new RiskResponse.RuleItem(r.ruleType(), r.threshold()))
                .toList();
        List<RiskResponse.RiskItem> risks = data.risks().stream()
                .map(r -> new RiskResponse.RiskItem(r.studentId(), r.nickname(),
                        r.weakPoints().stream()
                                .map(w -> new RiskResponse.WeakPointItem(w.name(), w.masteryScore()))
                                .toList(),
                        r.alertCount()))
                .toList();
        return Result.success(new RiskResponse(data.classId(), rules, risks));
    }

    /**
     * 自定义数字人教学风格（校验班级归属）
     */
    @PutMapping("/classes/{id}/style")
    public Result<String> style(@PathVariable Long id, @Valid @RequestBody ClassStyleRequest request) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long teacherId = SecurityUtils.currentUserId();
        teacherAppService.updateClassStyle(teacherId, id, request.getTone(), request.getStyle());
        return Result.success("班级教学风格已保存");
    }

    /**
     * 设置班级预警规则（阈值可配，防误报；校验班级归属）
     */
    @PostMapping("/teacher/classes/{id}/rules")
    public Result<String> saveRule(@PathVariable Long id, @Valid @RequestBody WarnRuleRequest request) {
        SecurityUtils.requireRole(UserRole.TEACHER);
        Long teacherId = SecurityUtils.currentUserId();
        teacherAppService.saveWarnRule(teacherId, id, request.getRuleType(), request.getThreshold());
        return Result.success("预警规则已保存");
    }

    private ClassResponse toClassResponse(SchoolClass schoolClass) {
        return new ClassResponse(schoolClass.getId(), schoolClass.getName(),
                schoolClass.getSubject(), schoolClass.getInviteCode(), schoolClass.getTeacherId());
    }
}
