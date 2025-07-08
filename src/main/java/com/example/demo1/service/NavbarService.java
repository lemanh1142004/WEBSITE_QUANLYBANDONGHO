package com.example.demo1.service;

import java.util.List;
import java.util.Optional;
import com.example.demo1.repository.NavbarRepository;
import org.springframework.stereotype.Service;

import com.example.demo1.entity.Product;

@Service
public class NavbarService {

    private final NavbarRepository navbarRepository;

    NavbarService(NavbarRepository navbarRepository) {
        this.navbarRepository = navbarRepository;
    }
		public List<Product> DHN(String name){
			return navbarRepository.findByCategory_Name(name);
		}
}
