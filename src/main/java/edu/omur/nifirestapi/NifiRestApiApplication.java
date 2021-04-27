package edu.omur.nifirestapi;

import edu.omur.nifirestapi.service.NifiHealthCheckService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NifiRestApiApplication implements CommandLineRunner {

    private final NifiHealthCheckService nifiHealthCheckService;

    public NifiRestApiApplication(NifiHealthCheckService nifiHealthCheckService) {
        this.nifiHealthCheckService = nifiHealthCheckService;
    }

    public static void main(String[] args) {
        SpringApplication.run(NifiRestApiApplication.class, args).close();
    }

    @Override
    public void run(String... args) {
        nifiHealthCheckService.startHealthCheck();
    }
}
