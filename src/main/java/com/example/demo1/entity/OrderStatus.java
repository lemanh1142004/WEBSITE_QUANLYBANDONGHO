package com.example.demo1.entity; // Hoặc package enums nếu bạn tạo riêng

public enum OrderStatus {
    PENDING,        // Đang chờ xử lý
    CONFIRMED,      // Đã xác nhận
    SHIPPED,        // Đã gửi hàng
    DELIVERED,      // Đã giao hàng
    CANCELLED       // Đã hủy
}