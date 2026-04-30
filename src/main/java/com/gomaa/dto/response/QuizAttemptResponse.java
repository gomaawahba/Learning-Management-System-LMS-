package com.gomaa.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class QuizAttemptResponse {
    private Long id;
    private Long quizId;
    private String quizTitle;
    private int score;
    private boolean passed;
    private int passScore;
    private LocalDateTime attemptedAt;
}