package com.gomaa.dto.response;

import com.gomaa.entity.CourseLevel;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class CourseResponse {
    private Long id;
    private String title;
    private String description;
    private String category;
    private CourseLevel level;
    private BigDecimal price;
    private boolean published;
    private String thumbnailUrl;
    private String instructorName;
    private Long instructorId;
    private double averageRating;
    private int totalStudents;
    private int totalLessons;
    private LocalDateTime createdAt;
}