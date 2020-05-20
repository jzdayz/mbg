package io.github.jzdayz.mbg;

import io.github.jzdayz.mbg.mb.PortUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class MbgApplication {

    static {
        System.setProperty("server.port", String.valueOf(PortUtils.port()));
    }

    public static void main(String[] args) {
        SpringApplication.run(MbgApplication.class, args);
    }

}
