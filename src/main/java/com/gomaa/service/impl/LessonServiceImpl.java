package com.gomaa.service.impl;

import com.gomaa.dto.request.LessonRequest;
import com.gomaa.dto.response.LessonResponse;
import com.gomaa.entity.Course;
import com.gomaa.entity.Lesson;
import com.gomaa.entity.Role;
import com.gomaa.entity.User;
import com.gomaa.exception.BusinessException;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.repository.CourseRepository;
import com.gomaa.repository.LessonRepository;
import com.gomaa.service.LessonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;
    private final CourseRepository courseRepository;

    @Override
    public LessonResponse createLesson(Long courseId, LessonRequest request, User instructor) {
        Course course = findCourse(courseId);

        validateInstructor(course, instructor);

        // ✅ Validate orderIndex uniqueness
        if (lessonRepository.existsByCourseIdAndOrderIndex(courseId, request.getOrderIndex())) {
            throw new BusinessException("Lesson order already exists for this course");
        }

        Lesson lesson = Lesson.builder()
                .course(course)
                .title(request.getTitle())
                .content(request.getContent())
                .videoUrl(request.getVideoUrl())
                .durationMinutes(request.getDurationMinutes())
                .orderIndex(request.getOrderIndex())
                .build();

        Lesson saved = lessonRepository.save(lesson);

        log.info("Lesson created: {} in course {}", saved.getId(), courseId);

        return mapToResponse(saved);
    }

    @Override
    public LessonResponse updateLesson(Long lessonId, LessonRequest request, User instructor) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

        validateInstructor(lesson.getCourse(), instructor);

        // ✅ Validate orderIndex (exclude current lesson)
        if (lesson.getOrderIndex() != request.getOrderIndex() &&
                lessonRepository.existsByCourseIdAndOrderIndex(
                        lesson.getCourse().getId(), request.getOrderIndex())) {
            throw new BusinessException("Lesson order already exists");
        }

        lesson.setTitle(request.getTitle());
        lesson.setContent(request.getContent());
        lesson.setVideoUrl(request.getVideoUrl());
        lesson.setDurationMinutes(request.getDurationMinutes());
        lesson.setOrderIndex(request.getOrderIndex());

        Lesson updated = lessonRepository.save(lesson);

        log.info("Lesson updated: {}", lessonId);

        return mapToResponse(updated);
    }

    @Override
    public void deleteLesson(Long lessonId, User instructor) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Lesson", lessonId));

        validateInstructor(lesson.getCourse(), instructor);

        lessonRepository.delete(lesson);

        log.info("Lesson deleted: {}", lessonId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LessonResponse> getLessonsByCourse(Long courseId) {
        return lessonRepository.findByCourseIdOrderByOrderIndexAsc(courseId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    // ================= Helpers =================

    private Course findCourse(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));
    }

    private void validateInstructor(Course course, User user) {
        if (user.getRole() == Role.ADMIN) return;

        if (!course.getInstructor().getId().equals(user.getId())) {
            throw new BusinessException("You are not allowed to modify this course");
        }
    }

    private LessonResponse mapToResponse(Lesson lesson) {
        return LessonResponse.builder()
                .id(lesson.getId())
                .courseId(lesson.getCourse().getId())
                .title(lesson.getTitle())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .durationMinutes(lesson.getDurationMinutes())
                .orderIndex(lesson.getOrderIndex())
                .build();
    }
}