package com.example.demo1.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: "Thanh toán khi nhận hàng", "Chuyển khoản ngân hàng"

    @Column(nullable = false)
    private boolean isActive; // Trạng thái hoạt động

    // Constructors
    public PaymentMethod() {
    }

    public PaymentMethod(String name, boolean isActive) {
        this.name = name;
        this.isActive = isActive;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}