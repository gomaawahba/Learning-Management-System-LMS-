package com.gomaa.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ReviewResponse {
    private Long id;
    private String studentName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;
}