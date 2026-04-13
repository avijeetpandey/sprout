package com.avijeet.sprout.repository.search;

import com.avijeet.sprout.entities.ProductIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface ProductSearchRepository extends ElasticsearchRepository<ProductIndex, String> {
    @Query("{\"multi_match\": {\"query\": \"?0\", \"fields\": [\"name\", \"description\"], \"fuzziness\": \"AUTO\"}}")
    List<ProductIndex> searchFuzzy(String query);
}
