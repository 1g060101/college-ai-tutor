package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.paper.PaperAppService;
import com.aistudy.tutor.domain.paper.model.Paper;
import com.aistudy.tutor.interfaces.dto.paper.CreatePaperRequest;
import com.aistudy.tutor.interfaces.dto.paper.CreatePaperResponse;
import com.aistudy.tutor.interfaces.dto.paper.ExamTipsResponse;
import com.aistudy.tutor.interfaces.dto.paper.HotPointResponse;
import com.aistudy.tutor.interfaces.dto.paper.PaperAnalysisResponse;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 备考冲刺接口：试卷上传 / 整卷讲解 / 高频考点串讲 / 答题技巧建议
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class PaperController {

    /** 答题建议免责声明 */
    private static final List<String> DISCLAIMERS = List.of("以上为高频训练参考，不承诺必考");

    private final PaperAppService paperAppService;

    /**
     * 上传卷面：创建试卷并逐条标注题目（题号+分值）
     */
    @PostMapping("/papers")
    public Result<CreatePaperResponse> createPaper(@Valid @RequestBody CreatePaperRequest request) {
        List<PaperAppService.PaperQuestionItem> items = request.getQuestions().stream()
                .map(q -> new PaperAppService.PaperQuestionItem(q.getQuestionId(), q.getScore()))
                .toList();
        Paper paper = paperAppService.createPaper(request.getCourseId(), request.getTitle(),
                request.getPaperType(), request.getYear(), request.getDurationMinutes(), items);
        return Result.success(new CreatePaperResponse(paper.getId(), request.getQuestions().size()));
    }

    /**
     * 真题/模拟卷讲解：AI 逐题讲解思路与考点
     */
    @GetMapping("/papers/{id}/analysis")
    public Result<PaperAnalysisResponse> analysis(@PathVariable Long id) {
        Long userId = SecurityUtils.currentUserId();
        PaperAppService.PaperAnalysisData data = paperAppService.analyzePaper(userId, id);
        return Result.success(new PaperAnalysisResponse(data.paperId(), data.title(), data.analysisText()));
    }

    /**
     * 高频考点串讲
     */
    @GetMapping("/exams/hot-points")
    public Result<HotPointResponse> hotPoints(@RequestParam(required = false) Long courseId) {
        Long userId = SecurityUtils.currentUserId();
        PaperAppService.HotPointsData data = paperAppService.hotPoints(userId, courseId);
        List<HotPointResponse.HotPointItem> items = data.points().stream()
                .map(p -> new HotPointResponse.HotPointItem(p.knowledgePointId(), p.name(), p.examFreq(), p.weight()))
                .toList();
        return Result.success(new HotPointResponse(data.courseId(), items, data.narrationText()));
    }

    /**
     * 答题技巧/时间分配（按薄弱点个性化）
     */
    @GetMapping("/exams/tips")
    public Result<ExamTipsResponse> tips() {
        Long userId = SecurityUtils.currentUserId();
        PaperAppService.TipsData data = paperAppService.examTips(userId);
        return Result.success(new ExamTipsResponse(data.tips(), DISCLAIMERS));
    }
}
