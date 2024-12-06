package kimjang.toolkit.solsol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SolsolApplication {
	public static void main(String[] args) {
		SpringApplication.run(SolsolApplication.class, args);
	}
}
