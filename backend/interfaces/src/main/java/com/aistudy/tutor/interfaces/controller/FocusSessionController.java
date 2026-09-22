package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.avatar.AvatarAppService;
import com.aistudy.tutor.domain.avatar.model.FocusSession;
import com.aistudy.tutor.interfaces.dto.avatar.FocusSessionRequest;
import com.aistudy.tutor.interfaces.dto.avatar.FocusSessionResponse;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 专注计时接口：创建 / 结束专注会话。
 */
@RestController
@RequestMapping("/api/v1/focus-sessions")
@RequiredArgsConstructor
public class FocusSessionController {

    private final AvatarAppService avatarAppService;

    /** 创建专注计时（status=RUNNING，start_time=now） */
    @PostMapping
    public Result<FocusSessionResponse> create(@Valid @RequestBody FocusSessionRequest request) {
        FocusSession session = avatarAppService.createFocusSession(SecurityUtils.currentUserId(),
                request.getCourseId(), request.getDurationMinutes());
        return Result.success(toResponse(session));
    }

    /** 结束专注：优先使用前台校准时长 frontendMinutes，否则按 start_time-end_time 计算 */
    @PutMapping("/{id}/end")
    public Result<FocusSessionResponse> end(@PathVariable Long id,
                                            @RequestParam(value = "frontendMinutes", required = false) Integer frontendMinutes) {
        FocusSession session = avatarAppService.endFocusSession(SecurityUtils.currentUserId(), id, frontendMinutes);
        return Result.success(toResponse(session));
    }

    private FocusSessionResponse toResponse(FocusSession session) {
        return new FocusSessionResponse(session.getId(), session.getCourseId(),
                session.getStartTime(), session.getEndTime(), session.getDurationMinutes(), session.getStatus());
    }
}
