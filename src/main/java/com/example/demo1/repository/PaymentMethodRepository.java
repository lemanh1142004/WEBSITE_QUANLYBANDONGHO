package com.example.demo1.repository;

import com.example.demo1.entity.PaymentMethod;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentMethodRepository extends JpaRepository<PaymentMethod, Integer> {
    // Có thể thêm các phương thức tìm kiếm PaymentMethod nếu cần
	Optional<PaymentMethod> findByName(String name);
}