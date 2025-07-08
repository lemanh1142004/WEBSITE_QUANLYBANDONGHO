package com.example.demo1.service;

import com.example.demo1.dto.OrderResponseDto; // Đảm bảo import này chính xác
import com.example.demo1.entity.CartItem;
import com.example.demo1.entity.Order;
import com.example.demo1.entity.PaymentMethod;
import com.example.demo1.entity.Users;
import com.example.demo1.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    // Phương thức tạo đơn hàng. Đảm bảo các tham số này khớp với implementation trong OrderServiceImpl.
    Order createOrder(Users user, List<CartItem> cartItems, String shippingAddress, String phoneNumber, String notes, PaymentMethod paymentMethod);

    // Phương thức lưu đơn hàng (có thể sử dụng cho update hoặc các mục đích khác)
    Order saveOrder(Order order);

    // Trả về danh sách đơn hàng của người dùng dưới dạng DTO (Data Transfer Object)
    // để tránh lộ thông tin entity ra ngoài và tùy chỉnh dữ liệu.
    List<OrderResponseDto> getOrdersByUser(Users user);

    // Trả về một đơn hàng cụ thể theo ID dưới dạng DTO, sử dụng Optional để xử lý trường hợp không tìm thấy.
    Optional<OrderResponseDto> getOrderById(Integer long1);

    // Phương thức cập nhật trạng thái đơn hàng
    Order updateOrderStatus(Integer orderId, String newStatus);

    // Phương thức xóa đơn hàng
    void deleteOrder(Integer orderId);    
    
}