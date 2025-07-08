package com.example.demo1.entity;

import jakarta.persistence.*; // Hoặc javax.persistence.* tùy thuộc phiên bản Spring Boot của bạn
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // ĐÃ THAY ĐỔI TỪ "`order`" THÀNH "orders"
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private Users user;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column(name = "shipping_address", nullable = false)
    private String shippingAddress;

    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(name = "total_amount", nullable = false, precision = 19, scale = 2) // Thêm precision và scale cho BigDecimal
    private BigDecimal totalAmount;

    @Column(name = "status", nullable = false)
    private String status; // Ví dụ: Pending, Processing, Shipped, Delivered, Cancelled

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Đảm bảo mappedBy trỏ đến trường 'order' trong OrderItem
    // CascadeType.ALL là quan trọng để khi lưu/cập nhật Order, các OrderItem cũng được xử lý.
    // orphanRemoval = true để tự động xóa OrderItem khi nó không còn được liên kết với một Order.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderDetails = new ArrayList<>();

    // Constructors
    public Order() {}

    // --- HELPER METHODS QUAN TRỌNG ĐỂ DUY TRÌ TÍNH NHẤT QUÁN CỦA MỐI QUAN HỆ HAI CHIỀU ---
    // Khi thêm một OrderItem vào danh sách, cũng thiết lập mối quan hệ ngược lại
    public void addOrderItem(OrderItem item) {
        if (item != null) {
            orderDetails.add(item);
            item.setOrder(this);
        }
    }

    // Khi xóa một OrderItem khỏi danh sách, cũng phá vỡ mối quan hệ ngược lại
    public void removeOrderItem(OrderItem item) {
        if (item != null) {
            orderDetails.remove(item);
            item.setOrder(null);
        }
    }
    // ---------------------------------------------------------------------------------

    // Getters and Setters (Đảm bảo tất cả các getter/setter đều có)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(String shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<OrderItem> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<OrderItem> orderDetails) {
        // Cẩn thận khi sử dụng setter này với @OneToMany.
        // Tốt hơn là sử dụng addOrderItem/removeOrderItem để duy trì tính nhất quán.
        // Nếu bạn muốn thay thế toàn bộ danh sách, hãy đảm bảo bạn cũng cập nhật mối quan hệ ngược lại.
        // Ví dụ:
        // this.orderDetails.forEach(item -> item.setOrder(null)); // Ngắt kết nối cũ
        // this.orderDetails.clear();
        // if (orderDetails != null) {
        //     orderDetails.forEach(item -> addOrderItem(item)); // Thêm mới và thiết lập kết nối
        // }
        this.orderDetails = orderDetails; // Cách đơn giản nhưng ít an toàn hơn với mối quan hệ 2 chiều
    }
}