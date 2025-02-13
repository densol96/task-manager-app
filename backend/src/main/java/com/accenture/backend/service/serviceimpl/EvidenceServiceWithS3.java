package com.accenture.backend.service.serviceimpl;

import com.accenture.backend.entity.Evidence;
import com.accenture.backend.exception.IllegalFileNameException;
import com.accenture.backend.exception.IllegalFileTypeException;
import com.accenture.backend.exception.FileUploadException;
import com.accenture.backend.repository.EvidenceRepository;
import com.accenture.backend.service.EvidenceService;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class EvidenceServiceWithS3 implements EvidenceService {

    private final EvidenceRepository evidenceRepository;

    @Value("${cloud.aws.credentials.access-key}")
    String s3AccessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    String s3SecretKey;

    @Value("${cloud.aws.bucket}")
    String bucketName;

    @Override
    public Evidence saveEvidence(MultipartFile file) {
        Evidence evidence = new Evidence();
        evidence.setUrl(saveFileToAWSS3Bucket(file));
        return evidenceRepository.save(evidence);
    }

    private String saveFileToAWSS3Bucket(MultipartFile file) {
        String originalFilename = validateFileName(file);
        String extension = extractFileExtension(originalFilename);
        String contentType = determineContentType(extension);
        String s3FileName = generateS3FileName(extension);

        AmazonS3 amazonS3Client = createS3Client();
        uploadFileToS3(amazonS3Client, file, s3FileName, contentType);

        return "https://" + bucketName + ".s3.amazonaws.com/" + s3FileName;
    }

    private String validateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            log.error("Invalid file name or missing extension");
            throw new IllegalFileNameException("Invalid file name or missing extension");
        }
        return originalFilename;
    }

    private String extractFileExtension(String originalFilename) {
        return originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
    }

    private String determineContentType(String extension) {
        return switch (extension) {
            case "jpeg", "jpg" -> "image/jpeg";
            case "png" -> "image/png";
            case "pdf" -> "application/pdf";
            default -> {
                log.error("Unsupported file type: {}", extension);
                throw new IllegalFileTypeException("Unsupported file type");
            }
        };
    }

    private String generateS3FileName(String extension) {
        return UUID.randomUUID() + "." + extension;
    }

    private AmazonS3 createS3Client() {
        BasicAWSCredentials awsCredentials = new BasicAWSCredentials(s3AccessKey, s3SecretKey);
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
                .withRegion(Regions.EU_NORTH_1)
                .build();
    }

    private void uploadFileToS3(AmazonS3 amazonS3Client, MultipartFile file, String s3FileName, String contentType) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(contentType);
        metadata.setContentLength(file.getSize());

        try (InputStream inputStream = file.getInputStream()) {
            log.info("Uploading file to S3 bucket: {}", s3FileName);
            amazonS3Client.putObject(new PutObjectRequest(bucketName, s3FileName, inputStream, metadata));
            log.info("File uploaded successfully: {}", s3FileName);
        } catch (IOException e) {
            log.error("Error while saving file to S3 bucket", e);
            throw new FileUploadException("Error while saving file to S3 bucket");
        }
    }
}
