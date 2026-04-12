package com.avijeet.sprout.services;

import com.avijeet.sprout.dto.ProductRequestDto;
import com.avijeet.sprout.dto.ProductResponseDto;
import com.avijeet.sprout.entities.Product;
import com.avijeet.sprout.exceptions.MethodArgumentNotValidException;
import com.avijeet.sprout.exceptions.ProductAlreadyExistsException;
import com.avijeet.sprout.exceptions.ProductNotFoundException;
import com.avijeet.sprout.repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final ProductRepository productRepository;

    /**
     * Adding new product into the ecosystem
     */
    @Transactional
    public ProductResponseDto addProduct(ProductRequestDto dto) {
        if(!productRepository.findByNameContainingIgnoreCase(dto.name()).isEmpty()) {
            throw new ProductAlreadyExistsException("Product with name already exists " + dto.name());
        }

        if(dto.name().isEmpty() || dto.description().isEmpty() || dto.sku() == null || dto.stockQuantity() <= 0) {
            log.error("Unable to save the product invalid arguments {}", dto.toString());
            throw new MethodArgumentNotValidException("Not a valid request to save the product");
        }

        Product product = toEntity(dto);
        ProductResponseDto savedProduct = toDto(productRepository.save(product));

        log.info("Product created with id {} ", savedProduct.id());

        return savedProduct;
    }

    /**
     * Finding the product by ID
     */
    public ProductResponseDto findById(Long id) {
        if(id == null) {
            throw new MethodArgumentNotValidException("Invalid id");
        }
        Optional<Product> optionalProduct = productRepository.findById(id);
        return optionalProduct.map(this::toDto).orElse(null);

    }

    /**
     * delete the product
     */
    public void deleteById(Long id) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if(optionalProduct.isPresent()) {
            productRepository.deleteById(id);
            log.info("Product with id {} deleted",id);
        } else {
            throw new ProductNotFoundException("Unable to find product with id");
        }
    }

    /**
     * get all products
     */
    public List<ProductResponseDto> getAllProducts() {
        return productRepository.findAll().stream().map(this::toDto).toList();
    }

    private ProductResponseDto toDto(Product product) {
        return new ProductResponseDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getSku(),
                product.getProductType(),
                product.getPrice(),
                product.getStockQuantity()
        );
    }

    private Product toEntity(ProductRequestDto dto) {
        return Product.builder()
                .name(dto.name())
                .description(dto.description())
                .sku(dto.sku())
                .productType(dto.productType())
                .price(dto.price())
                .stockQuantity(dto.stockQuantity())
                .build();
    }
}
