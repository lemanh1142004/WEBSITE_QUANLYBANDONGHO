package com.example.demo1.controller;

import com.example.demo1.dto.ProductDto;
import com.example.demo1.entity.Category;
import com.example.demo1.entity.Product;
import com.example.demo1.repository.CategoryRepository;
import com.example.demo1.repository.ProductRepository;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping("/user")
    public String listProducts(Model model, @RequestParam(name = "keyword", required = false) String keyword,HttpSession session) {
        List<Product> productsEntities;
        session.setAttribute("user" ,"user");
        if (keyword != null && !keyword.isEmpty()) {
            productsEntities = productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(keyword);
        } else {
            productsEntities = productRepository.findByDeletedFalse(); // ✅ chỉ lấy sản phẩm chưa xoá
        }

        List<ProductDto> productDtos = productsEntities.stream()
            .map(product -> new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStockQuantity(),
                product.getImageUrl(),
                product.getCategory(),
                product.getCreatedAt()
            ))
            .collect(Collectors.toList());

        model.addAttribute("products", productDtos);
        model.addAttribute("categories", categoryRepository.findAll());
        return "home";
    }


    @GetMapping("/products/{id}")
    public String viewProductDetail(@PathVariable("id") Integer id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            Product productEntity = productOptional.get();
            
            ProductDto productDto = new ProductDto(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getDescription(),
                productEntity.getPrice(),
                productEntity.getStockQuantity(), // Sử dụng getStockQuantity()
                productEntity.getImageUrl(),
                productEntity.getCategory(),
                productEntity.getCreatedAt() // Không cần .toLocalDateTime()
            );
            model.addAttribute("product", productDto);
        } else {
            return "redirect:/user";
        }
        return "product_detail";
    }
    
    
    @GetMapping("/products_detail/{id}")
    public String detail(@PathVariable int id ,  Model model) {
        Product product = productRepository.findById(id).orElse(null);
        model.addAttribute("product", product);
    	return "detail";
    }

}