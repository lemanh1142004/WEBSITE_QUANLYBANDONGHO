package com.example.demo1.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo1.entity.Category;
import com.example.demo1.entity.Product;

@Repository
public interface NavbarRepository extends JpaRepository<Product, Integer>  {
	 List<Product> findByCategory_Name(String name);
}
