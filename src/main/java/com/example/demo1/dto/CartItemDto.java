package com.example.demo1.dto;

import java.time.LocalDateTime; // Vẫn cần nếu bạn muốn dùng LocalDateTime ở các DTO khác

public class CartItemDto {
    private Integer id;
    private ProductDto product; // Sử dụng ProductDto
    private Integer quantity;
    // private LocalDateTime addedAt; // Đã bỏ đi nếu CartItem entity không có trường này

    // Constructors
    public CartItemDto() {}

    // Constructor để chuyển đổi từ CartItem Entity sang CartItemDto
    // Tham số `product` là com.example.demo1.entity.Product
    // Tham số `addedAt` đã được loại bỏ để khớp với CartItem entity hiện tại (không có trường addedAt)
    public CartItemDto(Integer id, com.example.demo1.entity.Product product, Integer quantity) {
        this.id = id;
        this.product = new ProductDto(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getImageUrl(),
            product.getCategory(), // Truyền Category entity vào ProductDto constructor
            product.getCreatedAt() // product.getCreatedAt() hiện tại đã là LocalDateTime, không cần .toLocalDateTime()
        );
        this.quantity = quantity;
        // this.addedAt = null; // Hoặc bỏ hoàn toàn nếu không có trường trong entity
    }


    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ProductDto getProduct() {
        return product;
    }

    public void setProduct(ProductDto product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    // Nếu bạn không có trường addedAt trong CartItem entity, hãy bỏ getter/setter này
    // public LocalDateTime getAddedAt() {
    //     return addedAt;
    // }

    // public void setAddedAt(LocalDateTime addedAt) {
    //     this.addedAt = addedAt;
    // }
}