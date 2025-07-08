package com.example.demo1.service;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo1.entity.Category;
import com.example.demo1.entity.Product;
import com.example.demo1.repository.CategoryRepository;
import com.example.demo1.repository.ProductRepository;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private CategoryRepository categoryRepo;

    // ✅ Dùng cho admin - xem tất cả (kể cả sản phẩm đã xoá)
    public List<Product> getAll() {
        return productRepo.findAll();
    }

    // ✅ Dùng cho người dùng - chỉ lấy sản phẩm còn hoạt động
    public List<Product> getAllActive() {
        return productRepo.findByDeletedFalse();
    }

    // ✅ Dùng cho admin - danh sách sản phẩm trong thùng rác
    public List<Product> getAllTrashed() {
        return productRepo.findByDeletedTrue();
    }

    // ✅ Tìm kiếm (chỉ các sản phẩm chưa bị xoá)
    public List<Product> searchByName(String keyword) {
        return productRepo.findByNameContainingIgnoreCaseAndDeletedFalse(keyword);
    }

    // ✅ Lưu sản phẩm (thêm hoặc sửa)
    @Transactional
    public void save(Product p) {
        if (p.getCreatedAt() == null) {
            p.setCreatedAt(LocalDateTime.now());
        }

        // Gắn danh mục đúng
        if (p.getCategory() != null && p.getCategory().getId() != null) {
            Category realCategory = categoryRepo.findById(p.getCategory().getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + p.getCategory().getId()));
            p.setCategory(realCategory);
        } else {
            p.setCategory(null);
        }

        // Nếu là sản phẩm mới => set deleted = false mặc định
        if (p.getDeleted() == null) {
            p.setDeleted(false);
        }

        productRepo.save(p);
    }

    public Product getById(int id) {
        return productRepo.findById(id).orElse(null);
    }

    // ✅ Xoá mềm - chuyển vào thùng rác
    @Transactional
    public void softDelete(int id) {
        Product product = productRepo.findById(id).orElse(null);
        if (product != null) {
            product.setDeleted(true);
            productRepo.save(product);
        }
    }

    // ✅ Khôi phục từ thùng rác
    @Transactional
    public void restore(int id) {
        Product product = productRepo.findById(id).orElse(null);
        if (product != null) {
            product.setDeleted(false);
            productRepo.save(product);
        }
    }

    // ✅ Xoá vĩnh viễn khỏi database
    @Transactional
    public boolean hardDelete(int id) {
        try {
            productRepo.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
