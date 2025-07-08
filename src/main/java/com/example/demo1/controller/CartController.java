package com.example.demo1.controller;

import com.example.demo1.entity.Cart;
import com.example.demo1.entity.Product;
import com.example.demo1.entity.Users;
import com.example.demo1.repository.ProductRepository;
import com.example.demo1.repository.UsersRepository;
import com.example.demo1.service.CartService;
import com.example.demo1.dto.CartDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes; // Thêm import này cho phương thức form submit

import java.util.Optional;
import java.math.BigDecimal;
import java.util.Map;

@Controller
// ***** KHÔNG CẦN @RequestMapping TẠI ĐÂY NẾU BẠN ĐÃ ĐẶT ĐƯỜNG DẪN ĐẦY ĐỦ TRÊN TỪNG PHƯƠNG THỨC *****
// HOẶC, NẾU MUỐN TẤT CẢ API BẮT ĐẦU BẰNG /api/cart, HÃY DÙNG: @RequestMapping("/api/cart")
// Ví dụ: @RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UsersRepository usersRepository;

    private Users getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            // Có thể ném một ngoại lệ cụ thể hơn hoặc chuyển hướng đến trang đăng nhập nếu đây là Web Controller
            throw new RuntimeException("User not authenticated or anonymous.");
        }
        String userEmail = authentication.getName();
        return usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + userEmail));
    }

    // Phương thức để hiển thị trang giỏ hàng (GET /cart) - VIEW CONTROLLER METHOD
    @GetMapping("/cart")
    public String viewCart(Model model) {
        try {
            Users currentUser = getCurrentUser();
            Cart userCart = cartService.getCartForUser(currentUser);

            BigDecimal totalPrice = BigDecimal.ZERO;
            if (userCart != null && userCart.getCartItems() != null) {
                for (com.example.demo1.entity.CartItem item : userCart.getCartItems()) {
                    if (item.getProduct() != null && item.getProduct().getPrice() != null && item.getQuantity() != null) {
                        totalPrice = totalPrice.add(item.getProduct().getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    }
                }
            }
            CartDTO cartDTO = new CartDTO(userCart != null ? userCart.getCartItems() : null, totalPrice);
            model.addAttribute("cart", cartDTO);

            return "user/cart";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            // Có thể chuyển hướng đến trang lỗi tùy chỉnh
            return "error-page";
        }
    }

    // ***** ĐIỀU CHỈNH QUAN TRỌNG: XỬ LÝ FORM SUBMIT TRUYỀN THỐNG ĐẾN /cart/add *****
    // Nếu nút "Thêm vào giỏ" của bạn là một form submit thông thường
    // và bạn muốn nó trỏ đến /cart/add, bạn cần một phương thức riêng cho nó.
    // Phương thức này sẽ không trả về ResponseEntity mà sẽ redirect.
    @PostMapping("/cart/add") // Đây là endpoint mà form HTML của bạn CÓ THỂ gọi
    public String addProductToCartFromForm(@RequestParam Integer productId,
                                           @RequestParam int quantity,
                                           RedirectAttributes redirectAttributes) {
        try {
            Users currentUser = getCurrentUser();
            Optional<Product> productOptional = productRepository.findById(productId);

            if (productOptional.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Sản phẩm không tồn tại.");
                return "redirect:/user"; // Hoặc trang bạn muốn redirect về
            }
            Product product = productOptional.get();

            cartService.addProductToCart(currentUser, product, quantity);
            redirectAttributes.addFlashAttribute("successMessage", "Sản phẩm đã được thêm vào giỏ hàng thành công!");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thêm vào giỏ hàng thất bại: " + e.getMessage());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Lỗi khi thêm vào giỏ hàng: " + e.getMessage());
        }
        return "redirect:/user"; // Chuyển hướng về trang danh sách sản phẩm
    }


    // API thêm sản phẩm vào giỏ hàng (POST /api/cart/add) - REST API ENDPOINT
    // Dùng @RestController nếu tất cả các phương thức trong class này đều là REST API
    // Hoặc giữ @Controller và dùng @ResponseBody cho từng phương thức API
    @PostMapping("/api/cart/add")
    @ResponseBody // Chỉ ra rằng giá trị trả về nên được ghi trực tiếp vào phản hồi HTTP body
    public ResponseEntity<String> addProductToCartApi(@RequestParam Integer productId, @RequestParam int quantity) {
        try {
            Users currentUser = getCurrentUser();
            Optional<Product> productOptional = productRepository.findById(productId);

            if (productOptional.isEmpty()) {
                return new ResponseEntity<>("Product not found", HttpStatus.NOT_FOUND);
            }
            Product product = productOptional.get();

            cartService.addProductToCart(currentUser, product, quantity);
            return new ResponseEntity<>("Product added to cart successfully!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            // Thay vì UNAUTHORIZED, có thể là FORBIDDEN nếu người dùng không có quyền
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    // API cập nhật số lượng sản phẩm trong giỏ hàng (PUT /api/cart/update)
    @PutMapping("/api/cart/update")
    @ResponseBody
    public ResponseEntity<String> updateCartItemQuantity(@RequestBody Map<String, Integer> payload) {
        Integer cartItemId = payload.get("cartItemId");
        Integer newQuantity = payload.get("quantity");

        if (cartItemId == null || newQuantity == null) {
            return new ResponseEntity<>("Missing cartItemId or quantity in request body.", HttpStatus.BAD_REQUEST);
        }

        try {
            Users currentUser = getCurrentUser();
            cartService.updateCartItemQuantity(currentUser, cartItemId, newQuantity);
            return new ResponseEntity<>("Cart item quantity updated successfully!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    // API xóa sản phẩm khỏi giỏ hàng (DELETE /api/cart/remove/{cartItemId})
    @DeleteMapping("/api/cart/remove/{cartItemId}")
    @ResponseBody
    public ResponseEntity<String> removeCartItem(@PathVariable Integer cartItemId) {
        try {
            Users currentUser = getCurrentUser();
            cartService.removeCartItem(currentUser, cartItemId);
            return new ResponseEntity<>("Cart item removed successfully!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
    }

    // API xóa toàn bộ giỏ hàng (DELETE /api/cart/clear)
    @DeleteMapping("/api/cart/clear")
    @ResponseBody
    public ResponseEntity<String> clearCart() {
        try {
            Users currentUser = getCurrentUser();
            cartService.clearUserCart(currentUser);
            return new ResponseEntity<>("Cart cleared successfully!", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}