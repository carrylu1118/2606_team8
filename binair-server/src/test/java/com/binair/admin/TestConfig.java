package com.binair.admin;

import jakarta.annotation.PostConstruct;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;

@Component
@SpringBootTest
public class TestConfig {

    @Value("${xml.path}")
    private String path;
    @Value("${xml.monitor.interval}")
    private long interval;

    @Test
    @PostConstruct
    public void init() {
        System.out.println("xml.path = " + path);
        System.out.println("xml.monitor.interval = " + interval);
    }
}
