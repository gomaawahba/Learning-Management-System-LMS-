package com.gomaa.service;

import com.gomaa.entity.User;

public interface CertificateService {
    String generateCertificate(Long courseId, User student);
}