package ru.practicum.shareit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;

import java.util.Collections;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class ShareItServer {
    @Value("${server.port}")
    private static int port;

    public static void main(String[] args) {
        //SpringApplication.run(ShareItServer.class, args);
        SpringApplication app = new SpringApplication(ShareItServer.class);
        app.setDefaultProperties(Collections
                .singletonMap("server.port", port));
        app.run(args);
    }
}