package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.ProductBulkRecord;
import com.avijeet.sprout.entities.Product;
import com.avijeet.sprout.enums.ProductType;
import com.avijeet.sprout.repository.ProductRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BulkProductService {
    private final ProductRepository productRepository;
    private final ObjectMapper objectMapper;

    private static final int CHUNK_SIZE = 500;

    @KafkaListener(
            topics = "product-bulk-ingestion",
            groupId = "bulk-processor-group",
            containerFactory = "bulkKafkaListenerContainerFactory"
    )
    @Transactional
    public void processBulkIngestion(String fileData, @Header(KafkaHeaders.RECEIVED_KEY) String jobId) {
        log.info("⚙️ Processing Bulk Job: {}", jobId);
        try {
            List<ProductBulkRecord> allRecords = objectMapper.readValue(fileData,
                    new TypeReference<List<ProductBulkRecord>>() {});

            log.info("📊 Parsed {} records for Job: {}", allRecords.size(), jobId);

            for (int i = 0; i < allRecords.size(); i += CHUNK_SIZE) {
                int end = Math.min(i + CHUNK_SIZE, allRecords.size());
                List<ProductBulkRecord> chunk = allRecords.subList(i, end);
                processChunk(chunk, jobId);
            }

            log.info("✅ Bulk Job {} completed successfully", jobId);
        } catch (Exception e) {
            log.error("Critical failure in Bulk Job {}: {}", jobId, e.getMessage());
            // Optional: log a snippet of the data to see what it looks like
            log.debug("Data snippet: {}", fileData.substring(0, Math.min(100, fileData.length())));
        }
    }

    private void processChunk(List<ProductBulkRecord> chunk, String jobId) {
        List<Product> entities = chunk.stream().map(record -> {
            Product p = new Product();
            p.setName(record.name());
            p.setDescription(record.description());
            p.setPrice(record.price());
            p.setStockQuantity(record.stockQuantity());
            p.setSku(record.sku());
            p.setProductType(ProductType.valueOf(record.productType()));
            return p;
        }).toList();

        productRepository.saveAll(entities);
        log.info("Saved chunk of {} products for Job: {}", entities.size(), jobId);
    }
}
