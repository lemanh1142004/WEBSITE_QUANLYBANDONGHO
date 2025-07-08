package com.example.demo1.service;

import com.example.demo1.entity.Cart;
import com.example.demo1.entity.CartItem;
import com.example.demo1.entity.Product;
import com.example.demo1.entity.Users;
import com.example.demo1.repository.CartItemRepository;
import com.example.demo1.repository.CartRepository;
import com.example.demo1.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.List; // Đảm bảo đã import List

@Service
public class CartServiceImpl implements CartService {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    @Transactional(readOnly = true)
    public Cart getCartForUser(Users user) {
        Optional<Cart> existingCart = cartRepository.findByUserWithItemsAndProducts(user);

        if (existingCart.isPresent()) {
            return existingCart.get();
        } else {
            Cart newCart = new Cart(user);
            return cartRepository.save(newCart);
        }
    }

    @Override
    @Transactional
    public CartItem addProductToCart(Users user, Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Sản phẩm không được null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Số lượng phải lớn hơn 0.");
        }

        Cart userCart = getCartForUser(user);

        // Đảm bảo lấy product từ DB để có thông tin tồn kho mới nhất
        Product managedProduct = productRepository.findById(product.getId())
                                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + product.getId()));

        Optional<CartItem> existingItemOptional = userCart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(managedProduct.getId()))
                .findFirst();

        CartItem cartItem;
        int currentQuantityInCart = 0;
        boolean isNewItem = false;

        if (existingItemOptional.isPresent()) {
            cartItem = existingItemOptional.get();
            currentQuantityInCart = cartItem.getQuantity();
        } else {
            cartItem = new CartItem();
            cartItem.setCart(userCart);
            cartItem.setProduct(managedProduct);
            isNewItem = true;
        }

        int totalRequestedQuantity = currentQuantityInCart + quantity;

        // KIỂM TRA TỒN KHO CUỐI CÙNG
        if (managedProduct.getStockQuantity() < totalRequestedQuantity) {
            throw new IllegalArgumentException("Số lượng sản phẩm '" + managedProduct.getName() + "' trong kho không đủ cho tổng số lượng yêu cầu (" + totalRequestedQuantity + " sản phẩm). Tồn kho hiện có: " + managedProduct.getStockQuantity() + ".");
        }

        // --- BỔ SUNG LOGIC TRỪ TỒN KHO KHI THÊM VÀO GIỎ HÀNG ---
        // Chỉ trừ đi số lượng mới được thêm vào, không phải tổng số lượng trong giỏ
        int quantityToDecrement = quantity;
        managedProduct.setStockQuantity(managedProduct.getStockQuantity() - quantityToDecrement);
        productRepository.save(managedProduct); // Lưu cập nhật tồn kho vào DB
        // --------------------------------------------------------

        // Nếu mọi thứ ổn, cập nhật số lượng trong CartItem
        cartItem.setQuantity(totalRequestedQuantity);

        // Thêm CartItem vào danh sách của Cart nếu đây là CartItem mới
        if (isNewItem) {
             userCart.addCartItem(cartItem);
        }

        return cartItemRepository.save(cartItem);
    }

    @Override
    @Transactional
    public CartItem updateCartItemQuantity(Users currentUser, Integer cartItemId, int quantity) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng với ID: " + cartItemId));

        if (!item.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền cập nhật giỏ hàng này.");
        }

        if (quantity <= 0) {
            // Nếu số lượng là 0 hoặc âm, coi như xóa sản phẩm
            removeCartItem(currentUser, cartItemId);
            return null;
        }

        Product managedProduct = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm: " + item.getProduct().getName()));

        int oldQuantityInCart = item.getQuantity(); // Số lượng cũ trong giỏ
        int quantityDifference = quantity - oldQuantityInCart; // Sự khác biệt

        // KIỂM TRA TỒN KHO (Sử dụng getStockQuantity())
        if (quantityDifference > 0 && managedProduct.getStockQuantity() < quantityDifference) {
            throw new IllegalArgumentException("Số lượng sản phẩm '" + managedProduct.getName() + "' trong kho không đủ để tăng thêm " + quantityDifference + " sản phẩm. Tồn kho hiện có: " + managedProduct.getStockQuantity() + ".");
        }

        // Cập nhật tồn kho dựa trên sự khác biệt
        managedProduct.setStockQuantity(managedProduct.getStockQuantity() - quantityDifference);
        productRepository.save(managedProduct);

        item.setQuantity(quantity);
        return cartItemRepository.save(item);
    }

    @Override
    @Transactional
    public void removeCartItem(Users currentUser, Integer cartItemId) {
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm trong giỏ hàng để xóa với ID: " + cartItemId));

        if (!item.getCart().getUser().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa mục giỏ hàng này.");
        }

        // Tăng lại số lượng tồn kho của sản phẩm (logic này đã đúng)
        Product product = item.getProduct();
        if (product != null && item.getQuantity() != null) {
            Product managedProduct = productRepository.findById(product.getId())
                                        .orElseThrow(() -> new IllegalStateException("Product not found during stock update."));
            managedProduct.setStockQuantity(managedProduct.getStockQuantity() + item.getQuantity());
            productRepository.save(managedProduct);
        }

        item.getCart().removeCartItem(item);
        cartItemRepository.delete(item);
    }

    @Override
    @Transactional
    public void clearUserCart(Users user) {
        Cart userCart = getCartForUser(user);

        if (userCart != null && !userCart.getCartItems().isEmpty()) {
            List<CartItem> itemsToClear = new java.util.ArrayList<>(userCart.getCartItems());

            // Tăng lại số lượng tồn kho cho từng sản phẩm trước khi xóa CartItem (logic này đã đúng)
            for (CartItem item : itemsToClear) {
                Product product = item.getProduct();
                if (product != null && item.getQuantity() != null) {
                    Product managedProduct = productRepository.findById(product.getId())
                                                .orElseThrow(() -> new IllegalStateException("Product not found during stock update."));
                    managedProduct.setStockQuantity(managedProduct.getStockQuantity() + item.getQuantity());
                    productRepository.save(managedProduct);
                }
            }

            cartItemRepository.deleteAll(itemsToClear);
            userCart.getCartItems().clear();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CartItem> getCartItemById(Integer cartItemId) {
        return cartItemRepository.findById(cartItemId);
    }
}