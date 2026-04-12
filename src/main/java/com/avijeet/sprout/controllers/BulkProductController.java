package com.avijeet.sprout.controllers;

import com.avijeet.sprout.dto.BulkUploadResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@Slf4j
public class BulkProductController {

    private final KafkaTemplate<String, String> stringKafkaTemplate;

    public BulkProductController(@Qualifier("stringKafkaTemplate") KafkaTemplate<String, String> stringKafkaTemplate) {
        this.stringKafkaTemplate = stringKafkaTemplate;
    }

    @PostMapping("/bulk-upload")
    public ResponseEntity<BulkUploadResponse> uploadProducts(@RequestParam("file") MultipartFile file) {
        String jobId = UUID.randomUUID().toString();
        log.info("🚀 Received bulk upload request. JobId: {}, FileName: {}", jobId, file.getOriginalFilename());

        try {
            String fileContent = new String(file.getBytes(), StandardCharsets.UTF_8);

            stringKafkaTemplate.send("product-bulk-ingestion", jobId, fileContent);

            return ResponseEntity.accepted().body(new BulkUploadResponse(
                    jobId, "ACCEPTED", "File is being processed in the background"
            ));
        } catch (IOException e) {
            log.error("Failed to read upload file for JobId: {}", jobId, e);
            throw new RuntimeException("Could not process file upload");
        }
    }
}