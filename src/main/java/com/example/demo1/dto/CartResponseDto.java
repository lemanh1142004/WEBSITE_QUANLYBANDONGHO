package com.example.demo1.dto;

import com.example.demo1.entity.CartItem;
import java.math.BigDecimal;
import java.util.List;

public class CartResponseDto {
    private List<CartItem> cartItems;
    private BigDecimal totalAmount;

    // Constructor
    public CartResponseDto(List<CartItem> cartItems, BigDecimal totalAmount) {
        this.cartItems = cartItems;
        this.totalAmount = totalAmount; // Đã sửa lỗi: phải là totalAmount
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount; // Đã sửa lỗi: phải là totalAmount
    }
}