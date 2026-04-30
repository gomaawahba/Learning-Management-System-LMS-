package com.gomaa.controller;

import com.gomaa.dto.response.ApiResponse;
import com.gomaa.security.SecurityUtil;
import com.gomaa.service.CertificateService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;
    private final SecurityUtil securityUtil;

    @GetMapping("/{courseId}")
    public ResponseEntity<ApiResponse<String>> generate(@PathVariable Long courseId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        certificateService.generateCertificate(courseId, securityUtil.getCurrentUser())));
    }
}
