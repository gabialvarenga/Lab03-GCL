package com.labGCL03.moeda_estudantil;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableAsync
@EnableScheduling
public class MoedaEstudantilApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoedaEstudantilApplication.class, args);
	}

}
