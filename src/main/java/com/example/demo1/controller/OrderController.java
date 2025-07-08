package com.example.demo1.controller;

import com.example.demo1.dto.OrderItemDto;
import com.example.demo1.dto.OrderResponseDto;
import com.example.demo1.entity.Cart;
import com.example.demo1.entity.CartItem;
import com.example.demo1.entity.Order;
import com.example.demo1.entity.PaymentMethod;
import com.example.demo1.entity.Product;
import com.example.demo1.entity.Users;
import com.example.demo1.repository.PaymentMethodRepository;
import com.example.demo1.repository.UsersRepository;
import com.example.demo1.service.CartService;
import com.example.demo1.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.NoSuchElementException; // Thêm import này

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    private Users getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            throw new RuntimeException("User not authenticated.");
        }
        String userEmail = authentication.getName();
        return usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
    }

    @PostMapping("/place")
    // @Transactional // @Transactional đã có trong service, không cần ở controller nếu không có logic giao dịch phức tạp hơn.
    public ResponseEntity<?> placeOrder(
            @RequestParam String shippingAddress,
            @RequestParam String phoneNumber, // ***** THÊM THAM SỐ NÀY *****
            @RequestParam(required = false) String notes, // ***** THÊM THAM SỐ NÀY, có thể là optional *****
            @RequestParam Integer paymentMethodId) {

        Users currentUser = getLoggedInUser();

        Cart userCart = cartService.getCartForUser(currentUser);
        if (userCart == null || userCart.getCartItems() == null || userCart.getCartItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Giỏ hàng của bạn đang trống hoặc không tồn tại. Không thể đặt hàng.");
        }
        List<CartItem> cartItems = userCart.getCartItems();

        PaymentMethod paymentMethod = paymentMethodRepository.findById(paymentMethodId)
                .orElseThrow(() -> new NoSuchElementException("Phương thức thanh toán không hợp lệ."));

        try {
            // Kiểm tra tồn kho trước khi tạo đơn hàng (đã có trong OrderServiceImpl nhưng kiểm tra ở đây để trả lỗi sớm hơn)
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                if (product == null) {
                    throw new IllegalArgumentException("Sản phẩm trong giỏ hàng không còn tồn tại.");
                }
                if (product.getStockQuantity() == null || product.getStockQuantity() < item.getQuantity()) {
                    throw new IllegalArgumentException("Sản phẩm '" + product.getName() + "' không đủ số lượng trong kho (" + product.getStockQuantity() + " sản phẩm có sẵn, bạn yêu cầu " + item.getQuantity() + ").");
                }
            }

            // ***** SỬA: TRUYỀN ĐỦ CÁC THAM SỐ CHO createOrder *****
            Order newOrder = orderService.createOrder(currentUser, cartItems, shippingAddress, phoneNumber, notes, paymentMethod);

            cartService.clearUserCart(currentUser); // Clear giỏ hàng của người dùng sau khi đặt hàng

            // Chuyển đổi Order entity sang OrderResponseDto
            // SỬ DỤNG PHƯƠNG THỨC convertToOrderResponseDto CỦA SERVICE ĐỂ ĐẢM BẢO TÍNH NHẤT QUÁN
            // Vì OrderServiceImpl đã có convertToOrderResponseDto, chúng ta có thể gọi nó từ service
            Optional<OrderResponseDto> responseDtoOptional = orderService.getOrderById(newOrder.getId());

            if (responseDtoOptional.isPresent()) {
                 return ResponseEntity.status(HttpStatus.CREATED).body(responseDtoOptional.get());
            } else {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Đặt hàng thành công nhưng không thể lấy thông tin đơn hàng đã tạo.");
            }

        } catch (IllegalArgumentException | NoSuchElementException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            // Log the exception for debugging purposes
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi đặt hàng: " + e.getMessage());
        }
    }

    @GetMapping("/my")
    public ResponseEntity<List<OrderResponseDto>> getMyOrders() {
        Users currentUser = getLoggedInUser();
        // ***** SỬA: Kiểu trả về từ service là List<OrderResponseDto> *****
        List<OrderResponseDto> orderDtos = orderService.getOrdersByUser(currentUser);
        return ResponseEntity.ok(orderDtos);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable Integer orderId) {
        Users currentUser = getLoggedInUser();

        // ***** SỬA: Kiểu trả về từ service là Optional<OrderResponseDto> *****
        Optional<OrderResponseDto> orderDtoOptional = orderService.getOrderById(orderId);

        if (orderDtoOptional.isPresent()) {
            OrderResponseDto orderDto = orderDtoOptional.get();
            // Kiểm tra quyền sở hữu đơn hàng
            if (!orderDto.getUserName().equals(currentUser.getName())) { // So sánh tên người dùng thay vì ID Users entity
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Bạn không có quyền truy cập đơn hàng này.");
            }
            return ResponseEntity.ok(orderDto);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable Integer orderId, @RequestParam String newStatus) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
            // Sau khi cập nhật, sử dụng service để chuyển đổi sang DTO để đảm bảo tính nhất quán
            Optional<OrderResponseDto> responseDtoOptional = orderService.getOrderById(updatedOrder.getId());
            if (responseDtoOptional.isPresent()) {
                 return ResponseEntity.ok(responseDtoOptional.get());
            } else {
                 return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Cập nhật trạng thái thành công nhưng không thể lấy thông tin đơn hàng.");
            }

        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Trạng thái đơn hàng không hợp lệ hoặc " + e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi cập nhật trạng thái: " + e.getMessage());
        }
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Integer orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.ok("Đơn hàng đã được xóa thành công.");
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Lỗi hệ thống khi xóa đơn hàng: " + e.getMessage());
        }
    }
}