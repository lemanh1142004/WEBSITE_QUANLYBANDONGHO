package com.example.demo1.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.demo1.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // ✅ Lấy sản phẩm chưa bị xoá
    List<Product> findByDeletedFalse();

    // ✅ Lấy sản phẩm đã bị xoá (thùng rác)
    List<Product> findByDeletedTrue();

    // ✅ Tìm kiếm tên (chỉ sản phẩm chưa bị xoá)
    List<Product> findByNameContainingIgnoreCaseAndDeletedFalse(String keyword);
}
