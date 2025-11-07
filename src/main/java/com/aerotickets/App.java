package com.aerotickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        var app = new SpringApplication(App.class);
        var ctx = app.run(args);

        ConfigurableEnvironment env = (ConfigurableEnvironment) ctx.getEnvironment();

        String appEnv = env.getProperty("SPRING_PROFILES_ACTIVE", "dev");
        String dbUrl  = env.getProperty("DB_URL",
                "jdbc:mysql://localhost:3306/aerotickets?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Bogota");
        String port   = env.getProperty("PORT", "8080");

        System.out.println("\n==============================================");
        System.out.println(" Aerotickets Backend iniciado");
        System.out.println(" Entorno: " + appEnv.toUpperCase());
        System.out.println(" Puerto : " + port);
        System.out.println(" DB URL : " + dbUrl);
        System.out.println(" Logs   : logs/aerotickets.log");
        System.out.println("==============================================\n");
    }
}