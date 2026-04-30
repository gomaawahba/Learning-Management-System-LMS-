package com.gomaa.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LessonRequest {
    @NotBlank
    private String title;
    private String content;
    private String videoUrl;
    private int durationMinutes;
    private int orderIndex;
}