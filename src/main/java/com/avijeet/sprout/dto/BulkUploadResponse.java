package com.avijeet.sprout.dto;

public record BulkUploadResponse(
        String jobId,
        String status,
        String message
) { }
