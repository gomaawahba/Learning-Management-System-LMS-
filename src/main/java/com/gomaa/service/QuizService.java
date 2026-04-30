package com.gomaa.service;

import com.gomaa.dto.request.QuizGenerateRequest;
import com.gomaa.dto.request.QuizSubmitRequest;
import com.gomaa.dto.response.QuizAttemptResponse;
import com.gomaa.dto.response.QuizResponse;
import com.gomaa.entity.User;

import java.util.List;

public interface QuizService {
    QuizResponse generateAiQuiz(Long courseId, QuizGenerateRequest request, User instructor);
    QuizAttemptResponse submitQuiz(QuizSubmitRequest request, User student);
    List<QuizResponse> getQuizzesByCourse(Long courseId);
    List<QuizAttemptResponse> getMyAttempts(User student);
}