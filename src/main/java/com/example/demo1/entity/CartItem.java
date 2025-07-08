package com.example.demo1.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // Quan hệ Many-to-One với Cart
    @ManyToOne(fetch = FetchType.LAZY) // Dùng LAZY là tốt để tránh tải quá nhiều dữ liệu
    @JoinColumn(name = "cart_id", nullable = false) // Tên cột khóa ngoại trong bảng cart_items
    private Cart cart; // <-- THÊM TRƯỜNG NÀY

    @ManyToOne(fetch = FetchType.EAGER) // Mối quan hệ Many-to-One với Product, nên là EAGER để dễ hiển thị thông tin sản phẩm
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

    // Constructors
    public CartItem() {
    }

    // Cập nhật constructor nếu cần
    public CartItem(Cart cart, Product product, Integer quantity) { // Cập nhật constructor
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    // Getter và Setter cho Cart (CẦN THIẾT)
    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) { // <-- THÊM SETTER NÀY
        this.cart = cart;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        if (product != null && product.getPrice() != null && quantity != null) {
            return product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        return BigDecimal.ZERO;
    }
}