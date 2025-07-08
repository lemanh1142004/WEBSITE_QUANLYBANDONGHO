package com.example.demo1.repository;

import com.example.demo1.entity.Cart; // Import Cart
import com.example.demo1.entity.CartItem;
import com.example.demo1.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    // Tìm tất cả các CartItem của một Cart (chứ không phải User trực tiếp nữa)
    List<CartItem> findByCart(Cart cart);

    // Tìm một CartItem cụ thể dựa trên Cart và Product
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    // Nếu bạn muốn tìm CartItems theo User (thông qua Cart)
    // List<CartItem> findByCart_User(Users user); // Ví dụ, nhưng tốt hơn là lấy Cart rồi dùng findByCart(Cart cart)
}