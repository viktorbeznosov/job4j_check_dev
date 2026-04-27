package ru.checkdev.notification;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.kafka.core.KafkaTemplate;

@OpenAPIDefinition(info = @Info(title = "Notification service", description = "Description"))
@SpringBootApplication
@EnableEurekaClient
@AllArgsConstructor
public class NtfSrv implements CommandLineRunner {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(NtfSrv.class);
        application.addListeners(new ApplicationPidFileWriter("./notification.pid"));
        application.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("========================================== START ==========================================");
        kafkaTemplate.send("job4j_checkdev", "test from job4j");
    }
}