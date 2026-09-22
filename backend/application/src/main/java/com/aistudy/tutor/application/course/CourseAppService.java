package com.aistudy.tutor.application.course;

import com.aistudy.tutor.domain.ai.AiCallResult;
import com.aistudy.tutor.domain.course.model.Course;
import com.aistudy.tutor.domain.course.model.KnowledgePoint;
import com.aistudy.tutor.domain.course.model.LearningPath;
import com.aistudy.tutor.domain.course.repository.CourseRepository;
import com.aistudy.tutor.domain.course.repository.KnowledgePointRepository;
import com.aistudy.tutor.domain.course.repository.LearningPathRepository;
import com.aistudy.tutor.infrastructure.aigateway.AiCallFacade;
import com.aistudy.tutor.shared.common.PageQuery;
import com.aistudy.tutor.shared.exception.BusinessException;
import com.aistudy.tutor.shared.result.PageResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 课程知识智能导学应用服务：课程列表 / 学习路径生成（拓扑排序 DAG）/ 知识点详情 / AI 口语化讲解
 */
@Service
@RequiredArgsConstructor
public class CourseAppService {

    private final CourseRepository courseRepository;
    private final KnowledgePointRepository knowledgePointRepository;
    private final LearningPathRepository learningPathRepository;
    private final AiCallFacade aiCallFacade;
    private final ObjectMapper objectMapper;

    /**
     * 课程列表（分页，不含已删除）
     */
    @Transactional(readOnly = true)
    public PageResult<Course> pageCourses(PageQuery query) {
        return courseRepository.page(query);
    }

    /**
     * 知识点详情，不存在抛 404
     */
    @Transactional(readOnly = true)
    public KnowledgePoint getKnowledgePoint(Long id) {
        return knowledgePointRepository.findById(id)
                .orElseThrow(() -> new BusinessException(404, "知识点不存在"));
    }

    /**
     * 口语化讲解：走 AI 门面生成 300 字内中文讲解（无 Key 时 Mock 兜底返回固定三段式）
     * 说明：内部会经 AiCallFacade 写入审计日志，需普通读写事务（readOnly 会导致回滚-only）。
     */
    @Transactional
    public String explain(Long userId, Long knowledgePointId) {
        KnowledgePoint kp = getKnowledgePoint(knowledgePointId);
        String system = "你是大学助教。请用口语化、带生活化例子的方式讲解以下知识点，控制在 300 字以内，使用中文。";
        StringBuilder userContent = new StringBuilder("知识点：「").append(kp.getName()).append("」");
        if (kp.getDescription() != null && !kp.getDescription().isBlank()) {
            userContent.append("\n简介：").append(kp.getDescription());
        }
        if (kp.getTags() != null && !kp.getTags().isBlank()) {
            userContent.append("\n标签：").append(kp.getTags());
        }
        AiCallResult result = aiCallFacade.chat(userId, system,
                List.of(Map.of("role", "user", "content", userContent.toString())), 0.7);
        return result.content();
    }

    /**
     * 获取课程学习路径：已有可用路径（user_id 匹配当前用户或为空）直接返回，
     * 否则按知识点 parent_id 拓扑排序（BFS 层序，带环保护）生成并保存。
     */
    @Transactional
    public Map<String, Object> getLearningPath(Long userId, Long courseId) {
        courseRepository.findById(courseId).orElseThrow(() -> new BusinessException(404, "课程不存在"));

        Optional<LearningPath> existing = learningPathRepository.findUsable(courseId, userId);
        LearningPath path;
        List<Long> orderedIds;
        if (existing.isPresent()) {
            path = existing.get();
            orderedIds = parseNodeOrder(path.getNodeOrder());
        } else {
            orderedIds = topoSort(courseId);
            path = learningPathRepository.save(new LearningPath(courseId, userId, "默认学习路径", writeNodeOrder(orderedIds)));
        }

        // 按拓扑顺序组装知识点节点
        Map<Long, KnowledgePoint> byId = new HashMap<>();
        for (KnowledgePoint kp : knowledgePointRepository.findAllActive()) {
            if (kp.getCourseId().equals(courseId)) {
                byId.put(kp.getId(), kp);
            }
        }
        List<Map<String, Object>> nodes = new ArrayList<>();
        for (Long id : orderedIds) {
            KnowledgePoint kp = byId.get(id);
            if (kp == null) {
                continue;
            }
            Map<String, Object> node = new LinkedHashMap<>();
            node.put("id", kp.getId());
            node.put("name", kp.getName());
            node.put("tags", kp.getTags());
            node.put("difficulty", kp.getDifficulty());
            node.put("parentId", kp.getParentId());
            nodes.add(node);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("pathId", path.getId());
        result.put("courseId", courseId);
        result.put("name", path.getName());
        result.put("nodes", nodes);
        return result;
    }

    /**
     * 课程知识点拓扑排序：parent_id 为 null 或父节点不在本课程集合内的作为根，
     * BFS 层序展开（visited 集合防死循环），环内节点兜底按 sortOrder 追加保证不丢。
     */
    private List<Long> topoSort(Long courseId) {
        List<KnowledgePoint> points = knowledgePointRepository.findAllActive().stream()
                .filter(kp -> kp.getCourseId().equals(courseId))
                .sorted(Comparator.comparingInt(KnowledgePoint::getSortOrder))
                .toList();
        Set<Long> ids = points.stream().map(KnowledgePoint::getId).collect(Collectors.toSet());

        // parent_id → children 邻接表，同父节点按 sortOrder 排序保证层内稳定
        Map<Long, List<KnowledgePoint>> children = new LinkedHashMap<>();
        for (KnowledgePoint kp : points) {
            children.computeIfAbsent(kp.getParentId(), k -> new ArrayList<>()).add(kp);
        }
        children.values().forEach(list -> list.sort(Comparator.comparingInt(KnowledgePoint::getSortOrder)));

        List<Long> order = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        Deque<KnowledgePoint> queue = new ArrayDeque<>();
        for (KnowledgePoint kp : points) {
            if (kp.getParentId() == null || !ids.contains(kp.getParentId())) {
                queue.add(kp);
            }
        }
        while (!queue.isEmpty()) {
            KnowledgePoint kp = queue.poll();
            if (!visited.add(kp.getId())) {
                continue;
            }
            order.add(kp.getId());
            for (KnowledgePoint child : children.getOrDefault(kp.getId(), List.of())) {
                if (!visited.contains(child.getId())) {
                    queue.add(child);
                }
            }
        }
        // 环保护兜底：仍未被访问的节点（如自引用环）按 sortOrder 追加
        for (KnowledgePoint kp : points) {
            if (!visited.contains(kp.getId())) {
                order.add(kp.getId());
            }
        }
        return order;
    }

    private String writeNodeOrder(List<Long> orderedIds) {
        try {
            return objectMapper.writeValueAsString(orderedIds);
        } catch (JsonProcessingException e) {
            throw new BusinessException(500, "学习路径序列化失败");
        }
    }

    private List<Long> parseNodeOrder(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<Long>>() {});
        } catch (Exception e) {
            return List.of();
        }
    }
}
