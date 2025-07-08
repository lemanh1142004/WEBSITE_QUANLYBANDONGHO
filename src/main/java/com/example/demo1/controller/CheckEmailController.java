package com.example.demo1.controller;

import com.example.demo1.service.CheckEmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/email") // không phải /api nữa
public class CheckEmailController {

    @Autowired
    CheckEmailService checkEmailService;

    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, HttpSession session) {
        String otp = checkEmailService.otp();
        session.setAttribute("otp", otp);
        checkEmailService.sendOtp(email, otp);
        session.setAttribute("error", "Mã OTP đã được gửi đến Email");
        return "auth/checkemail"; // quay lại form nhập OTP
    }

    @PostMapping("/verify-otp")
    public String verify( @RequestParam String otp,
                         HttpSession session) {
    	session.removeAttribute("error");
        String savedOtp = (String) session.getAttribute("otp");
        if (savedOtp != null && savedOtp.equals(otp) && session.getAttribute("forgot_mk")!=null){
        	session.removeAttribute("forgot_mk");
            session.removeAttribute("otp");
        	return "redirect:/resetMK"; 
        }

        else if (savedOtp != null && savedOtp.equals(otp)) {
            session.removeAttribute("otp");
            return "redirect:/login"; // 
        } else {
            return "auth/checkemail"; // ❌ sai mã thì quay lại trang xác thực
        }
    }
}
