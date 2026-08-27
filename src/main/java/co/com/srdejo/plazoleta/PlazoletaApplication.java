package co.com.srdejo.plazoleta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class PlazoletaApplication {

	static void main(String[] args) {
		SpringApplication.run(PlazoletaApplication.class, args);
	}

}
