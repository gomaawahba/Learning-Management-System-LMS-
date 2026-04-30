package com.gomaa.dto.response;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class QuizResponse {
    private Long id;
    private String title;
    private String description;
    private int passScore;
    private boolean aiGenerated;
    private List<QuestionResponse> questions;
}