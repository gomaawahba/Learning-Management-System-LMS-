package com.gomaa.controller;
import com.gomaa.security.SecurityUtil;
import com.gomaa.dto.request.QuizGenerateRequest;
import com.gomaa.dto.request.QuizSubmitRequest;
import com.gomaa.dto.response.ApiResponse;
import com.gomaa.dto.response.QuizAttemptResponse;
import com.gomaa.dto.response.QuizResponse;
import com.gomaa.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final SecurityUtil securityUtil;

    @PostMapping("/generate/{courseId}")
    public ResponseEntity<ApiResponse<QuizResponse>> generate(
            @PathVariable Long courseId,
            @RequestBody @Valid QuizGenerateRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        quizService.generateAiQuiz(courseId, request, securityUtil.getCurrentUser())));
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<QuizAttemptResponse>> submit(
            @RequestBody @Valid QuizSubmitRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        quizService.submitQuiz(request, securityUtil.getCurrentUser())));
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse<List<QuizResponse>>> getByCourse(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponse.success(quizService.getQuizzesByCourse(courseId)));
    }

    @GetMapping("/my-attempts")
    public ResponseEntity<ApiResponse<List<QuizAttemptResponse>>> myAttempts() {

        return ResponseEntity.ok(
                ApiResponse.success(
                        quizService.getMyAttempts(securityUtil.getCurrentUser())));
    }
}
