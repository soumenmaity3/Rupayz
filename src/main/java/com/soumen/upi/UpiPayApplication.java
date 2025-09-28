package com.soumen.upi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.soumen.upi")
@OpenAPIDefinition(
        info = @Info(
                title = "UPIPay API",
                version = "1.0",
                description = "API documentation for UPI Payment Service"
        )
)
public class UpiPayApplication {

	public static void main(String[] args) {
		SpringApplication.run(UpiPayApplication.class, args);
	}

}
