package com.gomaa.repository;

import com.gomaa.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LessonProgressRepository extends JpaRepository<LessonProgress, Long> {

    long countByStudentIdAndLessonCourseIdAndCompletedTrue(Long studentId, Long courseId);

    boolean existsByStudentIdAndLessonId(Long studentId, Long lessonId);
}