package com.example.demo1.repository;

import com.example.demo1.entity.Order;
import com.example.demo1.entity.OrderStatus;
import com.example.demo1.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph; // <-- THÊM DÒNG NÀY
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    // Phương thức này sẽ tải danh sách đơn hàng của người dùng,
    // đồng thời tự động tải (eagerly fetch) các chi tiết đơn hàng (orderDetails)
    // và thông tin phương thức thanh toán (paymentMethod)
    @EntityGraph(attributePaths = {"orderDetails", "paymentMethod"})
    List<Order> findByUser(Users user);

    // Ghi đè phương thức findById để nó cũng tự động tải các chi tiết đơn hàng và phương thức thanh toán
    // Điều này đảm bảo khi xem chi tiết một đơn hàng, mọi thông tin đều có sẵn
    @EntityGraph(attributePaths = {"orderDetails", "paymentMethod"})
    @Override // <-- THÊM @Override để chỉ rõ bạn đang ghi đè phương thức từ JpaRepository
    Optional<Order> findById(Integer id);

    // Bạn có thể giữ hoặc bỏ qua phương thức này, vì logic kiểm tra quyền đã có trong controller
    // @EntityGraph(attributePaths = {"orderDetails", "paymentMethod"})
    // Optional<Order> findByIdAndUser(Integer id, Users user);
    // Lấy danh sách đơn hàng theo trạng thái (ví dụ: PENDING, CONFIRMED, ...)
    List<Order> findByStatus(OrderStatus status);

    // Lấy danh sách đơn hàng theo ID người dùng
    List<Order> findByUserId(int userId);

    // Lấy danh sách đơn hàng theo email người dùng
    List<Order> findByUserEmail(String email);

}