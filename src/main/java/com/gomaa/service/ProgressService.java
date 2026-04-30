package com.gomaa.service;

import com.gomaa.entity.User;

public interface ProgressService {
    void markLessonCompleted(Long lessonId, User student);
    double getCourseProgress(Long courseId, User student);
}