package org.filestorage.service;

import jakarta.annotation.PostConstruct;
import org.filestorage.dto.SubmissionRequest;
import org.filestorage.exception.BadRequestException;
import org.filestorage.exception.NotFoundException;
import org.filestorage.model.Submission;
import org.filestorage.repository.SubmissionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class FileStorageService {

    private final S3Client s3Client;
    private final SubmissionRepository submissionRepository;
    private final String bucketName;

    public FileStorageService(S3Client s3Client,
                              SubmissionRepository submissionRepository,
                              @Value("${s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.submissionRepository = submissionRepository;
        this.bucketName = bucketName;
    }

    @PostConstruct
    public void init() {
        ensureBucketExists();
    }

    private void ensureBucketExists() {
        System.out.println("Checking bucket: " + bucketName);
        try {
            s3Client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
            System.out.println("Bucket exists!");
        } catch (Exception e) {
            System.out.println("Bucket check failed: " + e.getClass().getName());
            e.printStackTrace();

            if (e instanceof S3Exception s3 && s3.statusCode() == 404) {
                System.out.println("Bucket not found, creating...");
                s3Client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
                System.out.println("Bucket created");
            } else {
                throw e;
            }
        }
    }

    public Submission saveSubmission(MultipartFile file, SubmissionRequest req) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File must not be empty");
        }

        try {
            UUID submissionId = UUID.randomUUID();
            String cleanFileName = Path.of(file.getOriginalFilename())
                    .getFileName()
                    .toString();

            String key = "works/%s/students/%s/%s_%s".formatted(
                    req.getAssignmentId(),
                    req.getStudentIdentifier(),
                    submissionId,
                    cleanFileName
            );

            PutObjectRequest putReq = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    putReq,
                    RequestBody.fromBytes(file.getBytes())
            );

            Submission s = new Submission();
            s.setId(submissionId);
            s.setStudentName(req.getStudentName());
            s.setStudentIdentifier(req.getStudentIdentifier());
            s.setAssignmentId(req.getAssignmentId());
            s.setOriginalFileName(cleanFileName);
            s.setContentType(file.getContentType() != null
                    ? file.getContentType()
                    : "application/octet-stream");
            s.setSizeBytes(file.getSize());
            s.setBucket(bucketName);
            s.setObjectKey(key);
            s.setUploadedAt(LocalDateTime.now());

            return submissionRepository.save(s);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }

    public Submission getSubmission(UUID id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Submission not found: " + id));
    }

    public List<Submission> getSubmissionsByAssignment(String assignmentId) {
        return submissionRepository.findByAssignmentId(assignmentId);
    }

    public byte[] downloadFile(Submission submission) {
        try {
            GetObjectRequest getReq = GetObjectRequest.builder()
                    .bucket(submission.getBucket())
                    .key(submission.getObjectKey())
                    .build();

            ResponseBytes<?> objectBytes = s3Client.getObjectAsBytes(getReq);
            return objectBytes.asByteArray();
        } catch (NoSuchKeyException ex) {
            throw new NotFoundException("File not found in storage");
        }
    }
}
