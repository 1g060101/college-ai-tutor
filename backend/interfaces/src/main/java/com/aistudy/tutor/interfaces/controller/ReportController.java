package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.report.ReportAppService;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 学习报告接口：汇总 / 雷达 / 诊断 / 周期报告。
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportAppService reportAppService;

    /** 近 7 天学习汇总 */
    @GetMapping("/summary")
    public Result<Map<String, Object>> summary() {
        return Result.success(reportAppService.summary(SecurityUtils.currentUserId()));
    }

    /** 掌握度雷达 */
    @GetMapping("/radar")
    public Result<Map<String, Object>> radar() {
        return Result.success(reportAppService.radar(SecurityUtils.currentUserId()));
    }

    /** 学习诊断 */
    @GetMapping("/diagnosis")
    public Result<Map<String, Object>> diagnosis() {
        return Result.success(reportAppService.diagnosis(SecurityUtils.currentUserId()));
    }

    /** 周期报告：week / month，非法值由 appService 抛 400 */
    @GetMapping("/{period}")
    public Result<Map<String, Object>> report(@PathVariable String period) {
        return Result.success(reportAppService.report(SecurityUtils.currentUserId(), period));
    }
}
