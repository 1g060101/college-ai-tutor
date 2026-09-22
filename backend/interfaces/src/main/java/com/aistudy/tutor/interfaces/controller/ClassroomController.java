package com.aistudy.tutor.interfaces.controller;

import com.aistudy.tutor.application.classroom.ClassroomAppService;
import com.aistudy.tutor.interfaces.dto.classroom.MaterialResponse;
import com.aistudy.tutor.interfaces.dto.classroom.ReplayRequest;
import com.aistudy.tutor.interfaces.dto.classroom.ReplayResponse;
import com.aistudy.tutor.interfaces.dto.classroom.TranscriptResponse;
import com.aistudy.tutor.interfaces.dto.classroom.TranscribeRequest;
import com.aistudy.tutor.interfaces.security.SecurityUtils;
import com.aistudy.tutor.shared.result.Result;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

/**
 * 课堂辅助接口：课件上传解析/要点提炼、录音转写重点提炼、片段重讲、过期录音清理。
 */
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomAppService classroomAppService;

    /**
     * 上传课件/PPT/教材：纯文本可读内容直接解析为 DONE 并生成要点提炼，否则 FAILED
     */
    @PostMapping("/materials")
    public Result<MaterialResponse> uploadMaterial(@RequestParam("file") MultipartFile file,
                                                   @RequestParam(value = "courseId", required = false) Long courseId) throws IOException {
        Long userId = SecurityUtils.currentUserId();
        if (file == null || file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }
        Map<String, Object> data = classroomAppService.uploadMaterial(userId, file.getOriginalFilename(), file.getBytes(), courseId);
        return Result.success(toMaterialResponse(data));
    }

    /**
     * 上传课件→解析+要点提炼（与 /materials 同能力的别名端点）
     */
    @PostMapping("/materials/parse")
    public Result<MaterialResponse> parseMaterial(@RequestParam("file") MultipartFile file,
                                                  @RequestParam(value = "courseId", required = false) Long courseId) throws IOException {
        Long userId = SecurityUtils.currentUserId();
        if (file == null || file.isEmpty()) {
            return Result.fail(400, "文件不能为空");
        }
        Map<String, Object> data = classroomAppService.uploadMaterial(userId, file.getOriginalFilename(), file.getBytes(), courseId);
        return Result.success(toMaterialResponse(data));
    }

    /**
     * 录音转文字 + 重点提炼（演示环境无 ASR，直接接收用户粘贴的转写文本）
     */
    @PostMapping("/lectures/transcribe")
    public Result<TranscriptResponse> transcribe(@Valid @RequestBody TranscribeRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Map<String, Object> data = classroomAppService.transcribe(userId, request.getCourseId(),
                request.getLectureName(), request.getTranscriptText());
        return Result.success(new TranscriptResponse((Long) data.get("id"),
                (String) data.get("lectureName"), (String) data.get("summary")));
    }

    /**
     * 重讲某片段：定位关键词前后片段，AI 口语化重新讲解
     */
    @PostMapping("/lectures/{id}/replay")
    public Result<ReplayResponse> replay(@PathVariable Long id, @Valid @RequestBody ReplayRequest request) {
        Long userId = SecurityUtils.currentUserId();
        Map<String, Object> data = classroomAppService.replay(userId, id, request.getKeyword());
        return Result.success(new ReplayResponse((String) data.get("fragment"), (String) data.get("explanation")));
    }

    /**
     * 删除过期录音：软删
     */
    @DeleteMapping("/recordings/{id}")
    public Result<String> deleteRecording(@PathVariable Long id) {
        Long userId = SecurityUtils.currentUserId();
        classroomAppService.deleteTranscript(userId, id);
        return Result.success("已删除");
    }

    private MaterialResponse toMaterialResponse(Map<String, Object> data) {
        return new MaterialResponse((Long) data.get("id"), (String) data.get("fileName"), (String) data.get("fileType"),
                (String) data.get("status"), (String) data.get("errorMsg"), (String) data.get("summary"));
    }
}
