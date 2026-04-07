package com.avijeet.sprout.entities;

import com.avijeet.sprout.enums.ProductType;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "Product name cannot be blank")
    private String name;

    @Column(name = "description")
    @NotBlank(message = "Product description could not be blank")
    private String description;

    @Column(name = "price")
    @NotNull(message = "price cannot be null")
    @DecimalMin(value= "0.0" , inclusive = false, message = "Price must be greater than zero")
    private Double price;

    @Column(name = "stock_quantity")
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotBlank(message = "SKU is required")
    @Column(name = "sku", unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type")
    private ProductType productType;
}
