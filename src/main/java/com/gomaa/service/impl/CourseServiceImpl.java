package com.gomaa.service.impl;

import com.gomaa.dto.request.CourseRequest;
import com.gomaa.dto.response.CourseResponse;
import com.gomaa.entity.Course;
import com.gomaa.entity.Enrollment;
import com.gomaa.entity.Role;
import com.gomaa.entity.User;
import com.gomaa.exception.BusinessException;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.repository.CourseRepository;
import com.gomaa.repository.EnrollmentRepository;
import com.gomaa.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;

    @Override
    public CourseResponse createCourse(CourseRequest request, User instructor) {
        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(instructor)
                .category(request.getCategory())
                .level(request.getLevel())
                .price(request.getPrice())
                .published(request.isPublished())
                .thumbnailUrl(request.getThumbnailUrl())
                .build();

        Course saved = courseRepository.save(course);
        log.info("Course created: {} by instructor: {}", saved.getId(), instructor.getEmail());
        return mapToResponse(saved);
    }

    @Override
    public CourseResponse updateCourse(Long courseId, CourseRequest request, User currentUser) {
        Course course = findCourseById(courseId);
        checkCourseOwnership(course, currentUser);

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setLevel(request.getLevel());
        course.setPrice(request.getPrice());
        course.setPublished(request.isPublished());
        course.setThumbnailUrl(request.getThumbnailUrl());

        return mapToResponse(courseRepository.save(course));
    }

    @Override
    public void deleteCourse(Long courseId, User currentUser) {
        Course course = findCourseById(courseId);
        checkCourseOwnership(course, currentUser);
        courseRepository.delete(course);
        log.info("Course {} deleted by {}", courseId, currentUser.getEmail());
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponse getCourseById(Long id) {
        return mapToResponse(findCourseById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> getAllPublishedCourses(Pageable pageable) {
        return courseRepository.findByPublishedTrue(pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CourseResponse> searchCourses(String keyword, Pageable pageable) {
        return courseRepository.searchByKeyword(keyword, pageable).map(this::mapToResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getMyCourses(User instructor) {
        return courseRepository.findByInstructorId(instructor.getId())
                .stream().map(this::mapToResponse).toList();
    }

    @Override
    public CourseResponse enrollStudent(Long courseId, User student) {
        Course course = findCourseById(courseId);

        if (!course.isPublished()) {
            throw new BusinessException("Cannot enroll in an unpublished course");
        }
        if (enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BusinessException("Already enrolled in this course");
        }

        Enrollment enrollment = Enrollment.builder()
                .student(student)
                .course(course)
                .build();
        enrollmentRepository.save(enrollment);

        log.info("Student {} enrolled in course {}", student.getEmail(), courseId);
        return mapToResponse(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponse> getEnrolledCourses(User student) {
        return courseRepository.findEnrolledCoursesByStudentId(student.getId())
                .stream().map(this::mapToResponse).toList();
    }

    private Course findCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Course", id));
    }

    private void checkCourseOwnership(Course course, User user) {
        if (user.getRole() == Role.ADMIN) return;
        if (!course.getInstructor().getId().equals(user.getId())) {
            throw new BusinessException("You don't have permission to modify this course");
        }
    }

    public CourseResponse mapToResponse(Course course) {
        return CourseResponse.builder()
                .id(course.getId())
                .title(course.getTitle())
                .description(course.getDescription())
                .category(course.getCategory())
                .level(course.getLevel())
                .price(course.getPrice())
                .published(course.isPublished())
                .thumbnailUrl(course.getThumbnailUrl())
                .instructorName(course.getInstructor().getFullName())
                .instructorId(course.getInstructor().getId())
                .averageRating(course.getAverageRating())
                .totalStudents(course.getTotalStudents())
                .totalLessons(course.getLessons().size())
                .createdAt(course.getCreatedAt())
                .build();
    }
}