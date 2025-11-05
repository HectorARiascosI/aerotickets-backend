package com.aerotickets;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();

        String dbUrl = dotenv.get("DB_URL");
        String environment = dotenv.get("APP_ENV", "development");

        if (dbUrl != null) System.setProperty("DB_URL", dbUrl);

        System.out.println("\n==============================================");
        System.out.println("Iniciando Aerotickets Backend");
        System.out.println("Entorno: " + environment.toUpperCase());
        System.out.println("Base de datos: " + (dbUrl != null ? dbUrl : "jdbc:mysql://localhost:3306/aerotickets?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"));
        System.out.println("==============================================\n");

        try {
            SpringApplication.run(App.class, args);
            System.out.println("Servidor iniciado en http://localhost:8080");
            System.out.println("Logs activos en: logs/aerotickets.log\n");
        } catch (Exception e) {
            System.err.println("Error crítico al iniciar la aplicación:");
            e.printStackTrace();
        }
    }
}