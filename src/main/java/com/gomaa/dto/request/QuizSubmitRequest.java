package com.gomaa.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.Map;

@Data
public class QuizSubmitRequest {
    @NotNull
    private Long quizId;

    @NotNull
    private Map<Long, String> answers;
}