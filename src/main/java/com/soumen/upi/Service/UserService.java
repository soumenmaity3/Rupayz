package com.soumen.upi.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@Service
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;

    public String verify(String email, String password) {
        try {
            Authentication authentication =
                    authManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            if (authentication.isAuthenticated()) {
                return jwtService.generatorKey(email);
            }
            return "fail";
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            return "fail";
        }
    }
    public String getEmailFromToken(String token) {
        try {
            return jwtService.extractUsername(token);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
