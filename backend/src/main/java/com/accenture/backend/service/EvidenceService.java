package com.accenture.backend.service;

import com.accenture.backend.entity.Evidence;
import org.springframework.web.multipart.MultipartFile;

public interface EvidenceService {

    Evidence saveEvidence(MultipartFile file);
}
