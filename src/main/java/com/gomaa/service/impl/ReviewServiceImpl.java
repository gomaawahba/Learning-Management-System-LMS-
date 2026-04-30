package com.gomaa.service.impl;

import com.gomaa.dto.response.ReviewResponse;
import com.gomaa.entity.*;
import com.gomaa.exception.BusinessException;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.repository.*;
import com.gomaa.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void addReview(Long courseId, int rating, String comment, User student) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // ✅ must be enrolled
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BusinessException("You must be enrolled to review");
        }

        // ✅ rating validation
        if (rating < 1 || rating > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }

        // ✅ one review per student
        if (reviewRepository.findByCourseIdAndStudentId(courseId, student.getId()).isPresent()) {
            throw new BusinessException("Already reviewed");
        }

        Review review = Review.builder()
                .course(course)
                .student(student)
                .rating(rating)
                .comment(comment)
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getCourseReviews(Long courseId) {

        return reviewRepository.findByCourseId(courseId)
                .stream()
                .map(r -> ReviewResponse.builder()
                        .id(r.getId())
                        .studentName(r.getStudent().getFullName())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .toList();
    }
}