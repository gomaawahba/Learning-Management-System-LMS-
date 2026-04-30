package com.gomaa.controller;
import com.gomaa.security.SecurityUtil;
import com.gomaa.dto.response.ApiResponse;
import com.gomaa.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/progress")
@RequiredArgsConstructor
public class ProgressController {

    private final ProgressService progressService;
    private final SecurityUtil securityUtil;

    @PostMapping("/lesson/{lessonId}")
    public ResponseEntity<ApiResponse<Void>> complete(@PathVariable Long lessonId) {

        progressService.markLessonCompleted(lessonId, securityUtil.getCurrentUser());
        return ResponseEntity.ok(ApiResponse.success("Lesson completed", null));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<Double>> getProgress(@PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        progressService.getCourseProgress(courseId, securityUtil.getCurrentUser())));
    }
}
