package com.avijeet.sprout.dto.mappers;

import com.avijeet.sprout.dto.ProductRequestDto;
import com.avijeet.sprout.dto.ProductResponseDto;
import com.avijeet.sprout.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface ProductMapper {
    ProductResponseDto toDto(Product product);

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductRequestDto productRequestDto);
}
