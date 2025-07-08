package com.example.demo1.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo1.entity.OrderDetail;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {

    // Lấy danh sách chi tiết đơn hàng theo ID của đơn hàng
    List<OrderDetail> findByOrderId(Integer orderId);
}
