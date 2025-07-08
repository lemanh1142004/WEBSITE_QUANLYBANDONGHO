package com.example.demo1.service;

import java.util.List;

import javax.management.relation.Role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import com.example.demo1.entity.Users;
import com.example.demo1.repository.RegisterRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	@Autowired
	RegisterRepository registerRepository;
	
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        Users user = registerRepository.findByEmail(email);
        if(user== null) {
        	throw new UsernameNotFoundException("Error");
        }
        
		return new org.springframework.security.core.userdetails.User(
				user.getEmail(),
				user.getPassword(),
				List.of( new SimpleGrantedAuthority("ROLE_"+ user.getRole()))
				); // trả về user và password 
		
	}

}
