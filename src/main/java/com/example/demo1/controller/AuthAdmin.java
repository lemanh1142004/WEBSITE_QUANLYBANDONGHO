package com.example.demo1.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo1.dto.ProductDto;
import com.example.demo1.entity.*;
import com.example.demo1.repository.CategoryRepository;
import com.example.demo1.repository.ProductRepository;
import com.example.demo1.service.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class AuthAdmin {

    @Autowired private ProductService productService;
    @Autowired private OrderAdminService orderAdminService;
    @Autowired private CategoryService categoryService;
    @Autowired private UserService userService;
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;
    @GetMapping
    public String list(Model model,
                       @RequestParam(name = "keyword", required = false) String keyword,
                       HttpSession session) {
    	session.setAttribute("user", "admin");
    	List<Product> products = (keyword != null)
    	        ? productService.searchByName(keyword)
    	        : productService.getAllActive();
        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);
        session.setAttribute("user", "admin");
        return "admin/list";
    }

    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAll());
        return "admin/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Product product) {
        productService.save(product);
        return "redirect:/admin";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model) {
        Product product = productService.getById(id);
        if (product.getCategory() == null) {
            product.setCategory(new Category());
        }
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAll());
        return "admin/form";
    }

    // ✅ Thùng rác
    @GetMapping("/trash")
    public String viewTrash(Model model) {
        List<Product> trashedProducts = productService.getAllTrashed();
        model.addAttribute("products", trashedProducts);
        return "admin/trash";
    }

    @GetMapping("/restore/{id}")
    public String restoreProduct(@PathVariable int id) {
        productService.restore(id);
        return "redirect:/admin/trash";
    }
    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable int id, RedirectAttributes ra) {
        productService.softDelete(id);
        ra.addFlashAttribute("success", "Đã chuyển sản phẩm vào thùng rác.");
        return "redirect:/admin/trash";
    }
    @GetMapping("/hard-delete/{id}")
    public String hardDelete(@PathVariable int id, RedirectAttributes ra) {
        boolean deleted = productService.hardDelete(id);
        if (deleted) {
            ra.addFlashAttribute("success", "Đã xoá sản phẩm vĩnh viễn.");
        } else {
            ra.addFlashAttribute("error", "Không thể xoá sản phẩm vì đã được sử dụng trong đơn hàng.");
        }
        return "redirect:/admin/trash";
    }

    // ✅ Đơn hàng
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderAdminService.getAllOrders();
        List<String> Status = List.of("PENDING", "CONFIRMED", "SHIPPED", "DELIVERED", "CANCELLED");

        model.addAttribute("PENDING", orderAdminService.PENDING());
        model.addAttribute("CONFIRMED", orderAdminService.CONFIRMED());
        model.addAttribute("SHIPPED", orderAdminService.SHIPPED());
        model.addAttribute("DELIVERED", orderAdminService.DELIVERED());
        model.addAttribute("CANCELLED", orderAdminService.CANCELLED());
        model.addAttribute("Status", Status);
        model.addAttribute("orders", orders);

        return "admin/orders";
    }

    @PostMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable int id) {
        orderAdminService.deleteOrderById(id);
        return "redirect:/admin/orders";
    }

    @GetMapping("/user")
    public String user(Model model) {
        List<Users> user = userService.User();
        model.addAttribute("user", user);
        return "admin/user";
    }
    

 

    @GetMapping("/search")
    public String listProducts(Model model,
                               @RequestParam(name = "keyword", required = false) String keyword,
                               HttpSession session) {
        List<Product> productsEntities;

        if (keyword != null && !keyword.isEmpty()) {
            productsEntities = productRepository.findByNameContainingIgnoreCaseAndDeletedFalse(keyword);
        } else {
            productsEntities = productRepository.findByDeletedFalse();
        }

        model.addAttribute("products", productsEntities); // ✅ Dùng Product trực tiếp
        model.addAttribute("categories", categoryRepository.findAll());
        return "admin/list";
    }
    
}
