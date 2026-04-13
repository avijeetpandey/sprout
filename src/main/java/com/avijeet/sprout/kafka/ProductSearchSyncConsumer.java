package com.avijeet.sprout.kafka;

import com.avijeet.sprout.entities.Product;
import com.avijeet.sprout.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductSearchSyncConsumer {
    private final SearchService searchService;

    @KafkaListener(
            topics = "product-search-sync",
            groupId = "sprout-search-sync-group",
            containerFactory = "productKafkaListenerContainerFactory"
    )
    public void handleSync(Product product) {
        searchService.syncProduct(product);
    }
}
