package com.avijeet.sprout.controllers;

import com.avijeet.sprout.config.api.ApiResponse;
import com.avijeet.sprout.config.controller.BaseController;
import com.avijeet.sprout.constants.ApiConstants;
import com.avijeet.sprout.entities.ProductIndex;
import com.avijeet.sprout.services.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/search")
@RequiredArgsConstructor
public class SearchController extends BaseController {
    private final SearchService searchService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ProductIndex>>> search(@RequestParam String q) {
        return ok(ApiConstants.DONE_MESSAGE, searchService.search(q));
    }
}
