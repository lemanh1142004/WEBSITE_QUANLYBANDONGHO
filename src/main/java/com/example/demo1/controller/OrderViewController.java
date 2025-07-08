package com.example.demo1.controller;

import com.example.demo1.entity.Cart;
import com.example.demo1.entity.Order;
import com.example.demo1.entity.Users;
import com.example.demo1.entity.PaymentMethod;
import com.example.demo1.entity.Product;
import com.example.demo1.entity.CartItem;

import com.example.demo1.repository.UsersRepository;
import com.example.demo1.repository.PaymentMethodRepository;
import com.example.demo1.repository.ProductRepository;

import com.example.demo1.service.CartService;
import com.example.demo1.service.OrderService;

import com.example.demo1.dto.OrderItemDto;
import com.example.demo1.dto.OrderResponseDto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.NoSuchElementException;

@Controller
public class OrderViewController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private CartService cartService;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PaymentMethodRepository paymentMethodRepository;

    @Autowired
    private ProductRepository productRepository;

    private Users getLoggedInUser(RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Bạn cần đăng nhập để xem thông tin này.");
            throw new NotAuthenticatedException("User not authenticated.");
        }

        String userEmail = authentication.getName();

        return usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng trong hệ thống với email: " + userEmail));
    }

    private static class NotAuthenticatedException extends RuntimeException {
        private static final long serialVersionUID = 1L;
        public NotAuthenticatedException(String message) {
            super(message);
        }
    }


    @GetMapping("/my-orders")
    @Transactional(readOnly = true)
    public String viewMyOrders(Model model, RedirectAttributes redirectAttributes) {
        try {
            Users currentUser = getLoggedInUser(redirectAttributes);
            List<OrderResponseDto> orderDtos = orderService.getOrdersByUser(currentUser);

            model.addAttribute("orders", orderDtos);
            return "user/my-orders";

        } catch (NotAuthenticatedException e) {
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            e.printStackTrace();
            return "error-page";
        }
    }

    @GetMapping("/my-orders/{orderId}")
    @Transactional(readOnly = true)
    public String viewOrderDetail(@PathVariable Integer orderId, Model model, RedirectAttributes redirectAttributes) {
        try {
            Users currentUser = getLoggedInUser(redirectAttributes);
            Optional<OrderResponseDto> orderDtoOptional = orderService.getOrderById(orderId);

            if (orderDtoOptional.isEmpty()) {
                model.addAttribute("errorMessage", "Không tìm thấy đơn hàng.");
                return "error-page";
            }

            OrderResponseDto orderDto = orderDtoOptional.get();

            if (!orderDto.getUserName().equals(currentUser.getName())) {
                model.addAttribute("errorMessage", "Bạn không có quyền xem đơn hàng này.");
                return "error-page";
            }
            System.out.println("Số sản phẩm trong đơn hàng: " + orderDto.getOrderItems().size());
            orderDto.getOrderItems().forEach(item -> {
                System.out.println(item.getProductName());
            });
            orderDto.getOrderItems().forEach(item -> {
                System.out.println("Ảnh sản phẩm: " + item.getProductImage());
            });
            model.addAttribute("order", orderDto);
            return "user/order-detail";

        } catch (NotAuthenticatedException e) {
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            e.printStackTrace();
            return "error-page";
        }
    }

    @GetMapping("/checkout")
    @Transactional(readOnly = true)
    public String viewCheckout(Model model, RedirectAttributes redirectAttributes) {
        try {
            Users currentUser = getLoggedInUser(redirectAttributes);
            Cart userCart = cartService.getCartForUser(currentUser);

            if (userCart == null || userCart.getCartItems() == null || userCart.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm vào giỏ hàng trước khi thanh toán.");
                return "redirect:/cart";
            }

            BigDecimal totalPrice = BigDecimal.ZERO;
            for (CartItem item : userCart.getCartItems()) {
                if (item.getProduct() == null) {
                    System.err.println("Error: Product is null for CartItem ID: " + item.getId() + " in user's cart. Skipping this item.");
                    redirectAttributes.addFlashAttribute("warningMessage", "Một số sản phẩm trong giỏ hàng không còn tồn tại và đã bị bỏ qua.");
                    continue;
                }
                if (item.getProduct().getPrice() == null) {
                    System.err.println("Error: Product price is null for Product ID: " + item.getProduct().getId() + " in user's cart. Skipping this item.");
                    redirectAttributes.addFlashAttribute("warningMessage", "Một số sản phẩm trong giỏ hàng không có giá và đã bị bỏ qua.");
                    continue;
                }
                if (item.getQuantity() <= 0) {
                     System.err.println("Error: Quantity is zero or negative for CartItem ID: " + item.getId() + " in user's cart. Skipping this item.");
                     redirectAttributes.addFlashAttribute("warningMessage", "Một số sản phẩm trong giỏ hàng có số lượng không hợp lệ và đã bị bỏ qua.");
                     continue;
                }
                totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            }

            if (totalPrice.compareTo(BigDecimal.ZERO) <= 0 && !userCart.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Tổng giá trị giỏ hàng không hợp lệ. Vui lòng kiểm tra lại sản phẩm và giá.");
                return "redirect:/cart";
            }

            model.addAttribute("cartItems", userCart.getCartItems());
            model.addAttribute("totalPrice", totalPrice);

            List<PaymentMethod> paymentMethods = paymentMethodRepository.findAll();
            model.addAttribute("paymentMethods", paymentMethods);

            model.addAttribute("defaultFullName", currentUser.getName());
            model.addAttribute("defaultShippingAddress", currentUser.getAddress());
            model.addAttribute("defaultPhoneNumber", currentUser.getPhone());

            return "user/checkout";
        } catch (NotAuthenticatedException e) {
            return "redirect:/login";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            e.printStackTrace();
            return "error-page";
        }
    }

    @PostMapping("/place-order")
    public String placeOrder(
            @RequestParam("fullName") String fullName, // Đảm bảo bạn có dùng fullname này ở đâu đó nếu cần
            @RequestParam("address") String address,
            @RequestParam("phone") String phone,
            @RequestParam(value = "notes", required = false) String notes,
            @RequestParam("paymentMethodId") Integer paymentMethodId, // <-- ĐÃ SỬA TÊN PARAM VÀ KIỂU DỮ LIỆU
            RedirectAttributes redirectAttributes) {
        try {
            Users currentUser = getLoggedInUser(redirectAttributes);
            Cart userCart = cartService.getCartForUser(currentUser);

            if (userCart == null || userCart.getCartItems() == null || userCart.getCartItems().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Giỏ hàng của bạn đang trống. Vui lòng thêm sản phẩm vào giỏ hàng trước khi đặt hàng.");
                return "redirect:/cart";
            }

            // ... (Logic kiểm tra tồn kho và sản phẩm trong giỏ hàng giữ nguyên) ...

            Optional<PaymentMethod> paymentMethodOptional = paymentMethodRepository.findById(paymentMethodId); // <-- SỬ DỤNG findById
            if (paymentMethodOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Phương thức thanh toán không hợp lệ.");
                return "redirect:/checkout";
            }
            PaymentMethod selectedPaymentMethod = paymentMethodOptional.get();

            // SỬA CÁCH BẠN TRUYỀN THÔNG TIN VÀO orderService.createOrder
            // Đảm bảo createOrder nhận các tham số này.
            // Nếu fullName không được dùng để cập nhật user.name hoặc order.userName, bạn có thể bỏ qua nó hoặc chuyển nó vào notes nếu cần.
            Order savedOrder = orderService.createOrder(currentUser, userCart.getCartItems(), address, phone, notes, selectedPaymentMethod);

            cartService.clearUserCart(currentUser);

            redirectAttributes.addFlashAttribute("successMessage", "Đơn hàng của bạn (#" + savedOrder.getId() + ") đã được đặt thành công!");
            return "redirect:/my-orders";

        } catch (NotAuthenticatedException e) {
            return "redirect:/login";
        } catch (IllegalArgumentException | NoSuchElementException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt hàng thất bại: " + e.getMessage());
            return "redirect:/checkout";
        } catch (RuntimeException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Đặt hàng thất bại do lỗi hệ thống: " + e.getMessage());
            return "redirect:/checkout";
        }
    
    }
}