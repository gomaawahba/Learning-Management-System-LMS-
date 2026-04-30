package com.gomaa.repository;

import com.gomaa.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, Long> {

    // ✅ Get all lessons sorted
    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Long courseId);

    // ✅ Check if order index already exists (important validation)
    boolean existsByCourseIdAndOrderIndex(Long courseId, int orderIndex);

    // ✅ Optional: useful for safer queries
    Optional<Lesson> findByIdAndCourseId(Long lessonId, Long courseId);

    // ✅ Count lessons (useful for auto ترتيب)
    long countByCourseId(Long courseId);
}