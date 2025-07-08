package com.example.demo1.repository;

import com.example.demo1.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // Bạn có thể thêm các phương thức tìm kiếm Category ở đây nếu cần
}