package postly.example.postly;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class PostlyApplication {

    public static void main(String[] args) {
        SpringApplication.run(PostlyApplication.class, args);
    }

}
