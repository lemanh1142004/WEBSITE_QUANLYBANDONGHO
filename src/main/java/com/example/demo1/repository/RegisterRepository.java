package com.example.demo1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.stereotype.Repository;

import com.example.demo1.entity.Users;

@Repository
public interface RegisterRepository extends JpaRepository<Users,Integer> {
	Users findByEmail(String email);
}