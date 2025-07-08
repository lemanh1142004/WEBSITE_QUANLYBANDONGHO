package com.example.demo1.repository;

import com.example.demo1.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {
    // Có thể thêm các phương thức tìm kiếm OrderItem nếu cần
}