package com.gomaa.service.impl;

import com.gomaa.entity.*;
import com.gomaa.exception.BusinessException;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.repository.*;
import com.gomaa.service.ProgressService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class ProgressServiceImpl implements ProgressService {

    private final LessonRepository lessonRepository;
    private final LessonProgressRepository progressRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public void markLessonCompleted(Long lessonId, User student) {

        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

        Long courseId = lesson.getCourse().getId();

        // ✅ check enrollment
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BusinessException("You are not enrolled in this course");
        }

        // ✅ prevent duplicate
        if (progressRepository.existsByStudentIdAndLessonId(student.getId(), lessonId)) {
            return;
        }

        LessonProgress progress = LessonProgress.builder()
                .student(student)
                .lesson(lesson)
                .completed(true)
                .completedAt(LocalDateTime.now())
                .build();

        progressRepository.save(progress);
    }

    @Override
    @Transactional(readOnly = true)
    public double getCourseProgress(Long courseId, User student) {

        long totalLessons = lessonRepository.countByCourseId(courseId);

        if (totalLessons == 0) return 0;

        long completed = progressRepository
                .countByStudentIdAndLessonCourseIdAndCompletedTrue(
                        student.getId(), courseId);

        return Math.round(((double) completed / totalLessons) * 100);
    }
}