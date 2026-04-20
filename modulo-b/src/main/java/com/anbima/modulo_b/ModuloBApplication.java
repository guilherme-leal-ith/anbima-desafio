package com.anbima.modulo_b;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ModuloBApplication {
	public static void main(String[] args) {
		SpringApplication.run(ModuloBApplication.class, args);
	}
}
