package com.example.demo1.service;

import com.example.demo1.dto.OrderItemDto;
import com.example.demo1.dto.OrderResponseDto;
import com.example.demo1.entity.CartItem;
import com.example.demo1.entity.Order;
import com.example.demo1.entity.OrderItem;
import com.example.demo1.entity.PaymentMethod;
import com.example.demo1.entity.Product;
import com.example.demo1.entity.Users;

import com.example.demo1.repository.OrderRepository;
import com.example.demo1.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public Order createOrder(Users user, List<CartItem> cartItems, String shippingAddress, String phoneNumber, String notes, PaymentMethod paymentMethod) {
        if (cartItems.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order with an empty cart.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setNotes(notes);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("PENDING");

        BigDecimal calculatedTotalAmount = BigDecimal.ZERO; // Đổi tên biến để dễ phân biệt
        List<OrderItem> tempOrderItems = new ArrayList<>(); // Tạo danh sách tạm thời để tính tổng và kiểm tra

        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            if (product == null) {
                throw new IllegalArgumentException("Product not found for cart item with ID: " + cartItem.getProduct().getId());
            }
            if (product.getStockQuantity() == null || product.getStockQuantity() < cartItem.getQuantity()) {
                throw new IllegalArgumentException("Not enough stock for product: " + product.getName() + ". Available: " + product.getStockQuantity() + ", Requested: " + cartItem.getQuantity());
            }

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(product.getPrice());

            tempOrderItems.add(orderItem); // Thêm vào danh sách tạm thời
            calculatedTotalAmount = calculatedTotalAmount.add(product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));

            product.setStockQuantity(product.getStockQuantity() - cartItem.getQuantity());
            productRepository.save(product); // Lưu cập nhật số lượng sản phẩm
        }

        // GÁN totalAmount TRƯỚC KHI LƯU ORDER LẦN ĐẦU TIÊN
        order.setTotalAmount(calculatedTotalAmount); // DÒNG NÀY ĐƯỢC CHUYỂN LÊN TRÊN

        // BƯỚC 1: LƯU ORDER TRƯỚC ĐỂ NÓ CÓ ID VÀ CÓ totalAmount HỢP LỆ
        Order savedOrder = orderRepository.save(order); // Dòng này giờ sẽ không bị lỗi totalAmount là null

        // Sau khi Order đã có ID, mới thêm OrderItems vào và thiết lập mối quan hệ
        for (OrderItem item : tempOrderItems) {
            savedOrder.addOrderItem(item); // Phương thức addOrderItem sẽ thiết lập item.setOrder(savedOrder)
        }

        // Không cần lưu lại savedOrder lần nữa vì cascade đã xử lý các orderItems,
        // và totalAmount đã được thiết lập trước khi lưu lần 1.
        // Tuy nhiên, để đảm bảo transaction hoạt động tốt và tất cả thay đổi được commit,
        // việc lưu lại savedOrder ở cuối cũng không gây hại.
        // Tôi sẽ giữ lại để đảm bảo tính nhất quán với logic trước đó, mặc dù có thể không cần thiết.
        return orderRepository.save(savedOrder);
    }

    @Override
    public Order saveOrder(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDto> getOrdersByUser(Users user) {
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::convertToOrderResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<OrderResponseDto> getOrderById(Integer orderId) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);

        if (orderOpt.isEmpty()) {
            return Optional.empty();
        }

        Order order = orderOpt.get();

        List<OrderItemDto> itemDtos = order.getOrderDetails().stream()
        		.map(item ->new OrderItemDto(
        			    item.getProduct().getId(),
        			    item.getProduct().getName(),
        			    item.getProduct().getImageUrl(),
        			    item.getQuantity(),
        			    item.getPrice()
        			))
            .collect(Collectors.toList());

        OrderResponseDto dto = new OrderResponseDto(
            order.getId(),
            order.getUser(), // Truyền full Users entity vào
            order.getShippingAddress(),
            order.getPhoneNumber(),
            order.getTotalAmount(),
            order.getOrderDate(),
            order.getStatus(),
            order.getPaymentMethod(),
            order.getNotes(),
            itemDtos
        );

        return Optional.of(dto);
    }
    @Override
    @Transactional
    public Order updateOrderStatus(Integer orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new NoSuchElementException("Order not found with id: " + orderId));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrder(Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    private OrderResponseDto convertToOrderResponseDto(Order order) {
        OrderResponseDto dto = new OrderResponseDto();
        dto.setId(order.getId());
        dto.setUserName(order.getUser() != null ? order.getUser().getName() : "Người dùng không xác định");
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPhoneNumber(order.getPhoneNumber());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setOrderDate(order.getOrderDate());
        dto.setStatus(order.getStatus());
        dto.setNotes(order.getNotes());

        if (order.getPaymentMethod() != null) {
            dto.setPaymentMethodName(order.getPaymentMethod().getName());
        } else {
            dto.setPaymentMethodName("Phương thức thanh toán không xác định");
        }

        if (order.getOrderDetails() != null) {
            List<OrderItemDto> itemDtos = order.getOrderDetails().stream()
                    .map(this::convertToOrderItemDto)
                    .collect(Collectors.toList());
            dto.setOrderItems(itemDtos);
        } else {
            dto.setOrderItems(new ArrayList<>());
        }

        return dto;
    }

    private OrderItemDto convertToOrderItemDto(OrderItem orderItem) {
        OrderItemDto itemDto = new OrderItemDto();
        if (orderItem.getProduct() != null) {
            itemDto.setProductId(orderItem.getProduct().getId());
            itemDto.setProductName(orderItem.getProduct().getName());
            itemDto.setPrice(orderItem.getPrice());
        } else {
            itemDto.setProductId(null);
            itemDto.setProductName("Sản phẩm không rõ");
            itemDto.setPrice(BigDecimal.ZERO);
        }
        itemDto.setQuantity(orderItem.getQuantity());
        return itemDto;
    }
}