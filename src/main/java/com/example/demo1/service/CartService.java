package com.example.demo1.service;

import com.example.demo1.entity.Cart;
import com.example.demo1.entity.CartItem;
import com.example.demo1.entity.Product;
import com.example.demo1.entity.Users;

import java.util.Optional;

public interface CartService {
    Cart getCartForUser(Users user);
    CartItem addProductToCart(Users user, Product product, int quantity);
    CartItem updateCartItemQuantity(Users currentUser, Integer cartItemId, int quantity);
    void removeCartItem(Users currentUser, Integer cartItemId);
    void clearUserCart(Users user);
    Optional<CartItem> getCartItemById(Integer cartItemId);
}