package com.soumen.upi.Configration;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import com.soumen.upi.Service.JWTService;
import com.soumen.upi.Service.MyUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class JWTFilter extends OncePerRequestFilter{
    @Autowired
    private JWTService jwtService;
    @Autowired
    private MyUserDetailsService uService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader=request.getHeader("Authorization");
        String email=null;
        String token =null;
        try {
            if (authHeader!=null && authHeader.startsWith("Bearer ")) {
                token=authHeader.substring(7);
                email=jwtService.extractUsername(token);
            }

            if (email!=null&&SecurityContextHolder.getContext().getAuthentication()==null) {
                UserDetails userDetails=uService.loadUserByEmail(email);
                if (jwtService.validateToken(token, userDetails)) {
                     UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
