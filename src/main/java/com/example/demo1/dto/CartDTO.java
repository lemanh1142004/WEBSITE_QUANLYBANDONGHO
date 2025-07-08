package com.example.demo1.dto;

import com.example.demo1.entity.CartItem; // Đảm bảo import CartItem
import java.math.BigDecimal;
import java.util.List;

public class CartDTO {
	private List<CartItem> cartItems;
	private BigDecimal totalAmount;

	public CartDTO() {
	}

	public CartDTO(List<CartItem> cartItems, BigDecimal totalAmount) {
		this.cartItems = cartItems;
		this.totalAmount = totalAmount;
	}

	// Getters and Setters
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
		this.totalAmount = totalAmount;
	}
}