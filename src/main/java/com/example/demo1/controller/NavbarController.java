package com.example.demo1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.example.demo1.config.SecurityConfig;
import com.example.demo1.entity.Product;
import com.example.demo1.service.NavbarService;

@Controller
public class NavbarController {

	@Autowired
	NavbarService navbarService;

  
	@GetMapping("/DHN/{name}")
	public String DHN(@PathVariable("name") String name , Model model) {
		List<Product>DHN = navbarService.DHN(name);
		if(("Đồng hồ nam").equals(name)) {
			model.addAttribute("products", DHN);
			return "user/DHN";
		}
		else {
			model.addAttribute("products", DHN);
			return "user/DHNu";
		}
	
		}
	
}
