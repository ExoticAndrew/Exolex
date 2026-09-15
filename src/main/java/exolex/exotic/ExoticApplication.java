package exolex.exotic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ExoticApplication {

	public static void main(String[] args) {
		System.out.println("### DEBUG SPRING_PROFILES_ACTIVE = " + System.getenv("SPRING_PROFILES_ACTIVE"));
		SpringApplication.run(ExoticApplication.class, args);
	}

}