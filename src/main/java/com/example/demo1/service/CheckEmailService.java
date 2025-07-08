package com.example.demo1.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class CheckEmailService {


    @Autowired
    private JavaMailSender mailSender;

    public void sendOtp(String toEmail , String otp) {
    	 SimpleMailMessage message = new SimpleMailMessage();
    	    message.setTo(toEmail);
    	    message.setSubject("Test gửi OTP");
    	    message.setText("OTP" + otp);
    	    mailSender.send(message);
}
    public String otp() {
        int otp = (int)(Math.random() * 900000) + 100000;
        return String.valueOf(otp); // Trả về chuỗi 6 chữ số
    }

}
