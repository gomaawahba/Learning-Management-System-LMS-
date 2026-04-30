package com.gomaa.service;

import com.gomaa.dto.response.ReviewResponse;
import com.gomaa.entity.User;

import java.util.List;

public interface ReviewService {
    void addReview(Long courseId, int rating, String comment, User student);
    List<ReviewResponse> getCourseReviews(Long courseId);
}