package com.example.demo1.dto;

import com.example.demo1.entity.Users;
import com.example.demo1.entity.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrderResponseDto {
    private Integer id;
    private String userName;
    private String shippingAddress;
    private String phoneNumber; // Thêm trường số điện thoại
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
    private String status;
    private String paymentMethodName;
    private String notes; // Thêm trường ghi chú
    private List<OrderItemDto> orderItems;

    // 1. Constructor mặc định (quan trọng cho Spring/Jackson)
    public OrderResponseDto() {}

    // 2. Constructor với tất cả các trường DTO (được gọi khi tạo DTO hoàn chỉnh để trả về)
    public OrderResponseDto(Integer id, String userName, String shippingAddress, String phoneNumber,
                            BigDecimal totalAmount, LocalDateTime orderDate, String status,
                            String paymentMethodName, String notes, List<OrderItemDto> orderItems) {
        this.id = id;
        this.userName = userName;
        this.shippingAddress = shippingAddress;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethodName = paymentMethodName;
        this.notes = notes;
        this.orderItems = orderItems;
    }

    // 3. Constructor để chuyển đổi từ Entity Order (được gọi trong OrderServiceImpl)
    // Đây là constructor ánh xạ từ Order entity (có Users và PaymentMethod entities)
    public OrderResponseDto(Integer id, Users user, String shippingAddress, String phoneNumber,
                            BigDecimal totalAmount, LocalDateTime orderDate, String status,
                            PaymentMethod paymentMethod, String notes, List<OrderItemDto> orderItems) {
        this.id = id;
        this.userName = (user != null) ? user.getName() : "N/A"; // Lấy tên từ User entity
        this.shippingAddress = shippingAddress;
        this.phoneNumber = phoneNumber;
        this.totalAmount = totalAmount;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethodName = (paymentMethod != null) ? paymentMethod.getName() : "N/A"; // Lấy tên từ PaymentMethod entity
        this.notes = notes;
        this.orderItems = orderItems;
    }


    // Getters and Setters (đảm bảo đầy đủ cho tất cả các trường)
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getShippingAddress() { return shippingAddress; }
    public void setShippingAddress(String shippingAddress) { this.shippingAddress = shippingAddress; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public LocalDateTime getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDateTime orderDate) { this.orderDate = orderDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPaymentMethodName() { return paymentMethodName; }
    public void setPaymentMethodName(String paymentMethodName) { this.paymentMethodName = paymentMethodName; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public List<OrderItemDto> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItemDto> orderItems) { this.orderItems = orderItems; }
}