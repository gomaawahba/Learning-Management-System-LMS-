package com.gomaa.dto.request;

import com.gomaa.entity.CourseLevel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CourseRequest {
    @NotBlank(message = "Course title is required")
    private String title;

    private String description;

    private String category;

    private CourseLevel level = CourseLevel.BEGINNER;

    @NotNull
    @PositiveOrZero
    private BigDecimal price = BigDecimal.ZERO;

    private boolean published = false;

    private String thumbnailUrl;
}