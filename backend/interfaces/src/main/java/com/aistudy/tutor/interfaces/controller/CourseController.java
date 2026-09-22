package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.course.CourseAppService;
import com.aistudy.tutor.domain.course.model.Course;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.interfaces.dto.course.CourseResponse;
import com.aistudy.tutor.interfaces.dto.course.ExplainResponse;
import com.aistudy.tutor.interfaces.dto.course.KnowledgePointDetailResponse;
import com.aistudy.tutor.interfaces.dto.course.LearningPathResponse;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.result.PageResult;
import com.aistudy.tutor.shared.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 课程知识智能导学接口：课程列表 / 学习路径 / 知识点详情 / AI 口语化讲解
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CourseController {

    private final CourseAppService courseAppService;

    /**
     * 课程列表（分页，pageNum 从 1 开始，默认 1/10）
     */
    @GetMapping("/courses")
    public Result<PageResult<CourseResponse>> listCourses(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        PageResult<Course> page = courseAppService.pageCourses(new PageQuery(pageNum, pageSize));
        PageResult<CourseResponse> data = new PageResult<>(
                page.getList().stream().map(this::toCourseResponse).toList(),
                page.getTotal(), page.getPageNum(), page.getPageSize(), page.getTotalPages());
        return Result.success(data);
    }

    /**
     * 学习路径：按知识点 parent_id 拓扑排序生成无环路径（已有路径直接复用）
     */
    @GetMapping("/courses/{id}/path")
    public Result<LearningPathResponse> learningPath(@PathVariable Long id) {
        Map<String, Object> data = courseAppService.getLearningPath(SecurityUtils.currentUserId(), id);
        return Result.success(toPathResponse(data));
    }

    /**
     * 知识点详情，不存在返回 404
     */
    @GetMapping("/knowledge-points/{id}")
    public Result<KnowledgePointDetailResponse> knowledgePoint(@PathVariable Long id) {
        KnowledgePoint kp = courseAppService.getKnowledgePoint(id);
        return Result.success(toDetailResponse(kp));
    }

    /**
     * 口语化讲解（AI 生成，无 Key 时 Mock 兜底）；audioUrl 恒为 null（TTS 未配置）
     */
    @GetMapping("/knowledge-points/{id}/explain")
    public Result<ExplainResponse> explain(@PathVariable Long id) {
        String text = courseAppService.explain(SecurityUtils.currentUserId(), id);
        return Result.success(new ExplainResponse(text, null));
    }

    private CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(course.getId(), course.getName(), course.getSubject(),
                course.getDescription(), course.getDifficulty());
    }

    private KnowledgePointDetailResponse toDetailResponse(KnowledgePoint kp) {
        return new KnowledgePointDetailResponse(kp.getId(), kp.getCourseId(), kp.getParentId(),
                kp.getName(), kp.getDescription(), kp.getTags(), kp.getDifficulty(),
                kp.getWeight(), kp.getSortOrder());
    }

    @SuppressWarnings("unchecked")
    private LearningPathResponse toPathResponse(Map<String, Object> data) {
        List<Map<String, Object>> nodes = (List<Map<String, Object>>) data.get("nodes");
        List<LearningPathResponse.Node> nodeList = nodes.stream()
                .map(n -> new LearningPathResponse.Node((Long) n.get("id"), (String) n.get("name"),
                        (String) n.get("tags"), (Integer) n.get("difficulty"), (Long) n.get("parentId")))
                .toList();
        return new LearningPathResponse((Long) data.get("pathId"), (Long) data.get("courseId"),
                (String) data.get("name"), nodeList);
    }
}
