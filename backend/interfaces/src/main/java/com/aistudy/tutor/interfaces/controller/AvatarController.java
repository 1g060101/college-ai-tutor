package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.avatar.AvatarAppService;
import com.aistudy.tutor.domain.avatar.model.AvatarConfig;
import com.aistudy.tutor.interfaces.dto.avatar.AvatarChatRequest;
import com.aistudy.tutor.interfaces.dto.avatar.AvatarChatResponse;
import com.aistudy.tutor.interfaces.dto.avatar.AvatarConfigRequest;
import com.aistudy.tutor.interfaces.dto.avatar.AvatarConfigResponse;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数字人陪伴接口：形象配置获取/更新、陪伴对话。
 */
@RestController
@RequestMapping("/api/v1/avatar")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarAppService avatarAppService;

    /** 获取当前用户数字人配置（无记录返回默认配置） */
    @GetMapping("/config")
    public Result<AvatarConfigResponse> config() {
        AvatarConfig config = avatarAppService.getConfig(SecurityUtils.currentUserId());
        return Result.success(toConfigResponse(config));
    }

    /** 更新数字人配置（按 user_id upsert） */
    @PutMapping("/config")
    public Result<AvatarConfigResponse> updateConfig(@Valid @RequestBody AvatarConfigRequest request) {
        AvatarConfig config = avatarAppService.updateConfig(SecurityUtils.currentUserId(),
                request.getAvatarName(), request.getTone(), request.getStyle(), request.getVoice());
        return Result.success(toConfigResponse(config));
    }

    /** 陪伴对话：按数字人形象配置生成个性化回复 */
    @PostMapping("/chat")
    public Result<AvatarChatResponse> chat(@Valid @RequestBody AvatarChatRequest request) {
        String reply = avatarAppService.chat(SecurityUtils.currentUserId(), request.getCourseId(), request.getContent());
        return Result.success(new AvatarChatResponse(reply, null));
    }

    private AvatarConfigResponse toConfigResponse(AvatarConfig config) {
        return new AvatarConfigResponse(config.getId(), config.getAvatarName(),
                config.getTone(), config.getStyle(), config.getVoice());
    }
}
