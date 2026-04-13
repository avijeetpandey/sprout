package com.avijeet.sprout.services;

import com.avijeet.sprout.entities.Product;
import com.avijeet.sprout.entities.ProductIndex;
import com.avijeet.sprout.repository.search.ProductSearchRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SearchService {
    private final ProductSearchRepository productSearchRepository;

    public void syncProduct(Product product) {
        ProductIndex index = ProductIndex.builder()
                .id(product.getId().toString())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .sku(product.getSku())
                .productType(product.getProductType())
                .build();

        productSearchRepository.save(index);
        log.info("Product synced to Elasticsearch: {} ", product.getId());
    }

    public List<ProductIndex> search(String query) {
        return productSearchRepository.searchFuzzy(query);
    }
}
