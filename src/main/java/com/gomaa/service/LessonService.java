package com.gomaa.service;

import com.gomaa.dto.request.LessonRequest;
import com.gomaa.dto.response.LessonResponse;
import com.gomaa.entity.User;

import java.util.List;

public interface LessonService {
    LessonResponse createLesson(Long courseId, LessonRequest request, User instructor);
    LessonResponse updateLesson(Long lessonId, LessonRequest request, User instructor);
    void deleteLesson(Long lessonId, User instructor);
    List<LessonResponse> getLessonsByCourse(Long courseId);
}