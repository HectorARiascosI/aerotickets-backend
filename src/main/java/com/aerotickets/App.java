package com.aerotickets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        var app = new SpringApplication(App.class);
        var ctx = app.run(args);

        ConfigurableEnvironment env = (ConfigurableEnvironment) ctx.getEnvironment();

        String appEnv = env.getProperty("SPRING_PROFILES_ACTIVE", "dev");
        String dbUrl  = env.getProperty("DB_URL", "jdbc:postgresql://localhost:5432/aerotickets");
        String port   = env.getProperty("PORT", "8080");

        System.out.println("\n==============================================");
        System.out.println(" Aerotickets Backend iniciado");
        System.out.println(" Entorno: " + appEnv.toUpperCase());
        System.out.println(" Puerto : " + port);
        System.out.println(" DB URL : " + maskDbUrl(dbUrl));
        System.out.println(" Logs   : logs/aerotickets.log");
        System.out.println("==============================================\n");
    }

    /**
     * ✅ Muestra todos los endpoints REST registrados en la aplicación
     * (para confirmar que los controladores están activos).
     */
    @Bean
    public CommandLineRunner logAllEndpoints(ApplicationContext ctx) {
        return args -> {
            System.out.println("========= ENDPOINTS REGISTRADOS =========");
            try {
                // Usa específicamente el handler de controladores REST
                RequestMappingHandlerMapping mapping =
                        (RequestMappingHandlerMapping) ctx.getBean("requestMappingHandlerMapping");

                mapping.getHandlerMethods().forEach((key, value) -> {
                    System.out.println(key + " -> " + value);
                });
            } catch (Exception e) {
                System.out.println("⚠️ No se pudo obtener la lista de endpoints: " + e.getMessage());
            }
            System.out.println("=========================================");
        };
    }

    /**
     * Oculta cualquier contraseña presente en la URL JDBC para evitar exponer credenciales en logs.
     */
    private static String maskDbUrl(String url) {
        if (url == null) return "N/A";
        return url.replaceAll("(?i)(password=)[^&]+", "$1********");
    }
}