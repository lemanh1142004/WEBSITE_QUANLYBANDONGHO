package com.example.demo1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo1.entity.Users;
import com.example.demo1.repository.RegisterRepository;

@Service
public class RegisterService {
	@Autowired
	RegisterRepository registerRepository;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	   @Autowired
	    private JavaMailSender mailSender;
	
	public void Register(Users user) {
		 user.setPassword(passwordEncoder.encode(user.getPassword()));
		 user.setRole("USER");
		registerRepository.save(user);
	}
 

    public String sendOtp(String toEmail , String otp) {
    	 SimpleMailMessage message = new SimpleMailMessage();
    	    message.setTo(toEmail);
    	    message.setSubject("Test gửi OTP");
    	    message.setText("OTP" + otp);

    	    mailSender.send(message);
    	    return "Đã gửi";
}
    public String otp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp); // Trả về chuỗi 6 chữ số
    }
    public void resetMK(Users user ,String email,  String password) {
    	Users existingUser = registerRepository.findByEmail(email);
        if (existingUser != null) {
            existingUser.setPassword(passwordEncoder.encode(password));
            registerRepository.save(existingUser);
        }
    }
	
}
