package com.example.demo1.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
// import java.util.Set; // Nếu có quan hệ OneToMany với CartItem, Order

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name; // Đây có thể là full_name của bạn

    private String address;

    private String phone;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // THÊM TRƯỜNG ROLE
    // Bạn có thể dùng String hoặc Enum tùy theo cách bạn muốn quản lý vai trò
    @Column(nullable = false)
    private String role; // Ví dụ: "USER", "ADMIN"

    // Constructors
    public Users() {}

    public Users(String email, String password, String name, String address, String phone, LocalDateTime createdAt, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.createdAt = createdAt;
        this.role = role; // Gán role trong constructor
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // GETTER VÀ SETTER CHO ROLE (CẦN THIẾT)
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}