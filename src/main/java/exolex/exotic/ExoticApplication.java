package exolex.exotic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExoticApplication {

	public static void main(String[] args) {
		System.out.println("### DEBUG DB_HOST = " + System.getenv("DB_HOST"));
		System.out.println("### DEBUG DB_NAME = " + System.getenv("DB_NAME"));
		System.out.println("### DEBUG DB_USER = " + System.getenv("DB_USER"));
		SpringApplication.run(ExoticApplication.class, args);
	}

}