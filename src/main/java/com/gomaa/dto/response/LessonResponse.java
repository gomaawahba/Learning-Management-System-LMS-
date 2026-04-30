package com.gomaa.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LessonResponse {
    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private String videoUrl;
    private int durationMinutes;
    private int orderIndex;
}