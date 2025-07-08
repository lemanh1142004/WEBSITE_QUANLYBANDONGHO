package com.example.demo1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo1.entity.Order;
import com.example.demo1.repository.OrderRepository;

@Service
public class OrderDetailService{
	@Autowired
	OrderRepository orderRepository;
	
	  public Order getOrderById(int id) {
	        return orderRepository.findById(id).orElse(null);
	    }
}
