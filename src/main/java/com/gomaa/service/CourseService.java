package com.gomaa.service;

import com.gomaa.dto.request.CourseRequest;
import com.gomaa.dto.response.CourseResponse;
import com.gomaa.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseService {
    CourseResponse createCourse(CourseRequest request, User instructor);
    CourseResponse updateCourse(Long courseId, CourseRequest request, User currentUser);
    void deleteCourse(Long courseId, User currentUser);
    CourseResponse getCourseById(Long id);
    Page<CourseResponse> getAllPublishedCourses(Pageable pageable);
    Page<CourseResponse> searchCourses(String keyword, Pageable pageable);
    List<CourseResponse> getMyCourses(User instructor);
    CourseResponse enrollStudent(Long courseId, User student);
    List<CourseResponse> getEnrolledCourses(User student);
}