package com.example.demo1.dto;

import java.math.BigDecimal;

public class OrderItemDto {
    private Integer productId;
    private String productName;
    private String productImage;
    private int quantity;
    private BigDecimal price;
    private BigDecimal subtotal;

    // ✅ Constructor mặc định (bắt buộc cho Jackson hoặc Spring dùng DTO)
    public OrderItemDto() {}

    // ✅ Constructor dùng khi lấy dữ liệu từ OrderItem (bao gồm cả ID)
    public OrderItemDto(Integer productId, String productName, String productImage,
                        int quantity, BigDecimal price) {
        this.productId = productId;
        this.productName = productName;
        this.productImage = productImage;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }

    // ✅ Constructor dùng cho hiển thị (giao diện frontend), có sẵn `subtotal`
    public OrderItemDto(String productName, String productImage,
                        BigDecimal price, int quantity, BigDecimal subtotal) {
        this.productName = productName;
        this.productImage = productImage;
        this.price = price;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // ✅ Getters & Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateSubtotal(); // cập nhật lại subtotal nếu quantity thay đổi
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        updateSubtotal(); // cập nhật lại subtotal nếu price thay đổi
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    // ✅ Tính lại subtotal khi cần
    private void updateSubtotal() {
        if (this.price != null && this.quantity >= 0) {
            this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
