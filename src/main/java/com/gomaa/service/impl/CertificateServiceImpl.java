package com.gomaa.service.impl;

import com.gomaa.entity.Course;
import com.gomaa.entity.User;
import com.gomaa.exception.BusinessException;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.repository.CourseRepository;
import com.gomaa.repository.EnrollmentRepository;
import com.gomaa.service.CertificateService;
import com.gomaa.service.ProgressService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class CertificateServiceImpl implements CertificateService {

    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final ProgressService progressService;

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Override
    public String generateCertificate(Long courseId, User student) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        // ✅ لازم يكون enrolled
        if (!enrollmentRepository.existsByStudentIdAndCourseId(student.getId(), courseId)) {
            throw new BusinessException("Not enrolled in course");
        }

        // ✅ لازم يخلص الكورس
        double progress = progressService.getCourseProgress(courseId, student);
        if (progress < 100) {
            throw new BusinessException("Course not completed yet");
        }

        try {
            Path dir = Path.of(uploadDir, "certificates");
            Files.createDirectories(dir);

            String fileName = "certificate_" + student.getId() + "_" + courseId + ".pdf";
            Path filePath = dir.resolve(fileName);

            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(filePath.toFile()));

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 24, Font.BOLD);
            Font bodyFont = new Font(Font.HELVETICA, 16);

            document.add(new Paragraph("Certificate of Completion", titleFont));
            document.add(new Paragraph("\n"));

            document.add(new Paragraph("This certifies that:", bodyFont));
            document.add(new Paragraph(student.getFullName(), titleFont));

            document.add(new Paragraph("\nhas successfully completed the course:", bodyFont));
            document.add(new Paragraph(course.getTitle(), titleFont));

            document.add(new Paragraph("\nDate: " + java.time.LocalDate.now(), bodyFont));

            document.close();

            return "/uploads/certificates/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate");
        }
    }
}