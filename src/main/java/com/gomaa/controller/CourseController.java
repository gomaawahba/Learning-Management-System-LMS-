package com.gomaa.controller;

import com.gomaa.dto.request.CourseRequest;
import com.gomaa.dto.response.ApiResponse;
import com.gomaa.dto.response.CourseResponse;
import com.gomaa.security.SecurityUtil;
import com.gomaa.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final SecurityUtil securityUtil;

    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> create(
            @RequestBody @Valid CourseRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        courseService.createCourse(request, securityUtil.getCurrentUser())));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> update(
            @PathVariable Long id,
            @RequestBody @Valid CourseRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        courseService.updateCourse(id, request, securityUtil.getCurrentUser())));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {

        courseService.deleteCourse(id, securityUtil.getCurrentUser());
        return ResponseEntity.ok(ApiResponse.success("Deleted", null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CourseResponse>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseById(id)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseResponse>>> getAll(Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(courseService.getAllPublishedCourses(pageable)));
    }

    @PostMapping("/{id}/enroll")
    public ResponseEntity<ApiResponse<CourseResponse>> enroll(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        courseService.enrollStudent(id, securityUtil.getCurrentUser())));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<CourseResponse>>> myCourses() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        courseService.getMyCourses(securityUtil.getCurrentUser())));
    }
}
