// Trong AuthUser.java (hoặc tên controller tương ứng)
package com.example.demo1.controller;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.example.demo1.entity.Order;
import com.example.demo1.service.OrderDetailService;
import com.example.demo1.service.OrderService;

@Controller
@RequestMapping("/user")
public class AuthUser { // Hoặc UsersController, ProfileController...

    // ... (các autowired dependencies)

    // Đổi URL của phương thức này

}