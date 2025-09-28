package com.soumen.upi.Controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;

import java.io.IOException;

@RestController
@CrossOrigin("*")
@RequestMapping("/upi")
public class HelloController {
    

    @Operation(summary = "Redirect to Swagger UI")
    public void redirect(HttpServletResponse response) throws IOException {
        response.sendRedirect("/swagger-ui/index.html");
    }
    
    @GetMapping("/hello")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<?> hello() {
        return new ResponseEntity<>("Hello, UPI Payment Service is running!", HttpStatus.OK);
    }
}