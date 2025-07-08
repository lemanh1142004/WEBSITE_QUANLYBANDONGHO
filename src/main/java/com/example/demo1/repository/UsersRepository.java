package com.example.demo1.repository;

import com.example.demo1.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends JpaRepository<Users, Integer> {
    // SỬA: Thay findByUsername bằng findByEmail
    Optional<Users> findByEmail(String email);

    // Bạn có thể bỏ dòng findByUsername này đi HOẶC thêm trường username vào Users entity
    // Optional<Users> findByUsername(String username); // Đã bỏ hoặc sẽ yêu cầu thêm trường username vào Users entity
}