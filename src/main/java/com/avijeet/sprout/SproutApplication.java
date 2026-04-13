package com.avijeet.sprout;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(
        basePackages = "com.avijeet.sprout.repository",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                value = com.avijeet.sprout.repository.search.ProductSearchRepository.class
        )
)
public class SproutApplication {

    public static void main(String[] args) {
        SpringApplication.run(SproutApplication.class, args);
    }

}
