package org.ligson.ichat;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "org.ligson")
@Slf4j
public class Main {

    public static final long APP_START_TIME = System.currentTimeMillis();

    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args);
    }
}
