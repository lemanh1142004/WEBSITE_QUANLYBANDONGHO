package com.example.demo1.service;

import java.util.List;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo1.entity.Order;
import com.example.demo1.entity.OrderDetail;
import com.example.demo1.entity.OrderStatus;
import com.example.demo1.repository.OrderDetailRepository;
import com.example.demo1.repository.OrderRepository;

@Service
public class OrderAdminService {
	 private final OrderRepository orderRepository;
	    private final OrderDetailRepository orderDetailRepository;
	   
	    
	    
	    public OrderAdminService(OrderRepository orderRepository, OrderDetailRepository orderDetailRepository) {
	        this.orderRepository = orderRepository;
	        this.orderDetailRepository = orderDetailRepository;
	    }

	    public List<Order> getAllOrders() {
	        List<Order> orders = orderRepository.findAll();
	        System.out.println("➡️ Số lượng đơn hàng: " + orders.size());

	        for (Order order : orders) {
	            try {
	                if (order.getUser() != null) {
	                    System.out.println("👤 Khách hàng: " + order.getUser().getName() + 
	                                       " (" + order.getUser().getEmail() + ")");
	                } else {
	                    System.out.println("⚠️ User = null");
	                }

	                if (order.getPaymentMethod() != null) {
	                    System.out.println("💳 Thanh toán: " + order.getPaymentMethod().getName());
	                } else {
	                    System.out.println("⚠️ PaymentMethod = null");
	                }

	            } catch (Exception e) {
	                System.err.println("❌ Lỗi khi ép load dữ liệu: " + e.getMessage());
	            }
	        }

	        return orders;
	    }

	    public Optional<Order> getOrderById(Integer id) {
	        Optional<Order> optionalOrder = orderRepository.findById(id);

	        optionalOrder.ifPresent(order -> {
	            try {
	                if (order.getUser() != null) {
	                    order.getUser().getName(); // ép load
	                }
	                if (order.getPaymentMethod() != null) {
	                    order.getPaymentMethod().getName(); // ép load
	                }
	            } catch (Exception e) {
	                System.err.println("❌ Lỗi khi ép load Order by ID: " + e.getMessage());
	            }
	        });

	        return optionalOrder;
	    }

	    public List<OrderDetail> getOrderDetailsByOrderId(Integer orderId) {
	        return orderDetailRepository.findByOrderId(orderId);
	    }

	    public void updateOrderStatus(Integer orderId, String status) {
	        Optional<Order> optionalOrder = orderRepository.findById(orderId);
	        if (optionalOrder.isPresent()) {
	            Order order = optionalOrder.get();
	            order.setStatus(status);
	            orderRepository.save(order);
	        } else {
	            throw new RuntimeException("❌ Không tìm thấy đơn hàng có ID = " + orderId);
	        }
	    }

	    public void deleteOrderById(Integer id) {
	        if (orderRepository.existsById(id)) {
	            orderRepository.deleteById(id);
	        } else {
	            throw new RuntimeException("❌ Không tìm thấy đơn hàng để xoá, ID = " + id);
	        }
	    }

	    public List<Order> getOrdersByStatus(OrderStatus status) {
	        return orderRepository.findByStatus(status);
	    }

	    public List<Order> getOrdersByUserId(int userId) {
	        return orderRepository.findByUserId(userId);
	    }

	    public List<Order> getOrdersByUserEmail(String email) {
	        return orderRepository.findByUserEmail(email);
	    }
	    public int PENDING() {
	        List<Order> orders = orderRepository.findAll();
	        int count = 0;
	        for (Order order : orders) {
	            if ("PENDING".equals(order.getStatus())) {
	                count++;
	            }
	        }
	        return count;
	    }

	    public int SHIPPED() {
	        List<Order> orders = orderRepository.findAll();
	        int count = 0;
	        for (Order order : orders) {
	            if ("SHIPPED".equals(order.getStatus())) {
	                count++;
	            }
	        }
	        return count;
	    }

	    public int CONFIRMED() {
	        List<Order> orders = orderRepository.findAll();
	        int count = 0;
	        for (Order order : orders) {
	            if ("CONFIRMED".equals(order.getStatus())) {
	                count++;
	            }
	        }
	        return count;
	    }

	    public int DELIVERED() {
	        List<Order> orders = orderRepository.findAll();
	        int count = 0;
	        for (Order order : orders) {
	            if ("DELIVERED".equals(order.getStatus())) {
	                count++;
	            }
	        }
	        return count;
	    }

	    public int CANCELLED() {
	        List<Order> orders = orderRepository.findAll();
	        int count = 0;
	        for (Order order : orders) {
	            if ("CANCELLED".equals(order.getStatus())) {
	                count++;
	            }
	        }
	        return count;
	    }
}
