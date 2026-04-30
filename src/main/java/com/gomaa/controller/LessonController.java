package com.gomaa.controller;
import com.gomaa.security.SecurityUtil;
import com.gomaa.dto.request.LessonRequest;
import com.gomaa.dto.response.ApiResponse;
import com.gomaa.dto.response.LessonResponse;
import com.gomaa.service.LessonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lessons")
@RequiredArgsConstructor
public class LessonController {

    private final LessonService lessonService;
    private final SecurityUtil securityUtil;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<LessonResponse>> create(
            @PathVariable Long courseId,
            @RequestBody @Valid LessonRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        lessonService.createLesson(courseId, request, securityUtil.getCurrentUser())));
    }

    @PutMapping("/{lessonId}")
    public ResponseEntity<ApiResponse<LessonResponse>> update(
            @PathVariable Long lessonId,
            @RequestBody @Valid LessonRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        lessonService.updateLesson(lessonId, request, securityUtil.getCurrentUser())));
    }

    @DeleteMapping("/{lessonId}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long lessonId) {

        lessonService.deleteLesson(lessonId, securityUtil.getCurrentUser());
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<LessonResponse>>> getLessons(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponse.success(lessonService.getLessonsByCourse(courseId)));
    }
}
