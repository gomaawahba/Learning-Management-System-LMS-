package com.gomaa.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class QuizGenerateRequest {

    @NotBlank(message = "Topic is required")
    private String topic;

    @Min(3) @Max(20)
    private int numberOfQuestions = 5;

    private String difficulty = "INTERMEDIATE";

    private String additionalContext;
}