package com.example.demo1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.demo1.entity.Product;
import com.example.demo1.entity.Users;
import com.example.demo1.service.CategoryService;
import com.example.demo1.service.CustomUserDetailsService;
import com.example.demo1.service.ProductService;
import com.example.demo1.service.RegisterService;

import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final CustomUserDetailsService customUserDetailsService;

    private final SecurityFilterChain apiSecurityFilterChain;
	  @Autowired
	    private ProductService productService;

	    @Autowired
	    private CategoryService categoryService;
	@Autowired
	RegisterService registerService;

    AuthController(SecurityFilterChain apiSecurityFilterChain, CustomUserDetailsService customUserDetailsService) {
        this.apiSecurityFilterChain = apiSecurityFilterChain;
        this.customUserDetailsService = customUserDetailsService;
    }

	@GetMapping("/register")
	public String home(Model model) {
		model.addAttribute("user", new Users());
		return "auth/register";
	}

	@PostMapping("/register/save")
	public String register(@ModelAttribute Users user, @RequestParam String email, HttpSession session) {
		registerService.Register(user);
		String otp = registerService.otp();
		session.setAttribute("otp", otp);
		registerService.sendOtp(email, otp);
		session.setAttribute("error", "Mã OTP đã được gửi đến Email");
		session.setAttribute("email", email);
		return "auth/checkemail"; // quay lại form nhập OTP

	}

	@GetMapping("/resetMK")
	public String resetMK(HttpSession session) {
		return "auth/resetMK";
	}

	@GetMapping("/input_email")
	public String inputEmail(HttpSession session) {
		session.setAttribute("forgot_mk", "forgot_mk");
		return "auth/input-email";
	}
	@GetMapping("/vetifyEmail")
	public String Email() {
		return "auth/checkemail";
	}

	@PostMapping("/reset-password")
	public String resetMK(@RequestParam String newPassword, @RequestParam String email, @ModelAttribute Users user) {
		registerService.resetMK(user, email, newPassword);
		return "auth/login";
	}

	@GetMapping("/login")
	public String login(Model model) {
		model.addAttribute("user", new Users());
		return "auth/login";
	}



	  @GetMapping
	    public String list(Model model,
	                       @RequestParam(name = "keyword", required = false) String keyword,
	                       HttpSession session) {

	    	List<Product> products = (keyword != null)
	    	        ? productService.searchByName(keyword)
	    	        : productService.getAllActive();
	        model.addAttribute("products", products);
	        model.addAttribute("keyword", keyword);
	        return "admin/list";
	    }
	  



}
