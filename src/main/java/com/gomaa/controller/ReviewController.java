package com.gomaa.controller;
import com.gomaa.security.SecurityUtil;
import com.gomaa.dto.request.ReviewRequest;
import com.gomaa.dto.response.ApiResponse;
import com.gomaa.dto.response.ReviewResponse;
import com.gomaa.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final SecurityUtil securityUtil;

    @PostMapping("/{courseId}")
    public ResponseEntity<ApiResponse<Void>> addReview(
            @PathVariable Long courseId,
            @RequestBody @Valid ReviewRequest request) {

        reviewService.addReview(
                courseId,
                request.getRating(),
                request.getComment(),
                securityUtil.getCurrentUser()
        );

        return ResponseEntity.ok(ApiResponse.success("Review added", null));
    }

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getReviews(
            @PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponse.success(reviewService.getCourseReviews(courseId)));
    }
}
