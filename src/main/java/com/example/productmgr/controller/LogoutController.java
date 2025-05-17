package com.example.productmgr.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// このコントローラはSpring Security標準のログアウト機能を使用するため現在は使用していません
// @Controller  // コメントアウトして無効化
public class LogoutController {

    private static final Logger logger = LoggerFactory.getLogger(LogoutController.class);

    // @GetMapping("/perform-logout")  // コメントアウトして無効化
    public String logoutPage(HttpServletRequest request, HttpServletResponse response) {
        logger.info("Logout request received");
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            logger.info("Logging out user: {}", auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        
        logger.info("Redirecting to login page with logout parameter");
        return "redirect:/login?logout";
    }
}