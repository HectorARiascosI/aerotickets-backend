package com.aerotickets;

import com.aerotickets.constants.AppMessages;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.CommandLineRunner;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;

@SpringBootApplication
@EnableScheduling
public class App {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(App.class);
        ApplicationContext ctx = app.run(args);

        ConfigurableEnvironment env = (ConfigurableEnvironment) ctx.getEnvironment();

        String appEnv = env.getProperty(AppMessages.ENV_PROFILE_KEY, AppMessages.DEFAULT_PROFILE);
        String dbUrl = env.getProperty(AppMessages.ENV_DB_URL_KEY, AppMessages.DEFAULT_DB_URL);
        String port = env.getProperty(AppMessages.ENV_PORT_KEY, AppMessages.DEFAULT_PORT);

        System.out.println(AppMessages.BANNER_HEADER_SEPARATOR);
        System.out.println(AppMessages.BANNER_TITLE);
        System.out.println(AppMessages.BANNER_ENV_PREFIX + appEnv.toUpperCase());
        System.out.println(AppMessages.BANNER_PORT_PREFIX + port);
        System.out.println(AppMessages.BANNER_DB_URL_PREFIX + maskDbUrl(dbUrl));
        System.out.println(AppMessages.BANNER_LOGS_LINE);
        System.out.println(AppMessages.BANNER_FOOTER_SEPARATOR);
    }

    @Bean
    public CommandLineRunner logAllEndpoints(ApplicationContext ctx) {
        return args -> {
            System.out.println(AppMessages.ENDPOINTS_HEADER);
            try {
                Map<String, RequestMappingHandlerMapping> mappings =
                        ctx.getBeansOfType(RequestMappingHandlerMapping.class);

                mappings.forEach((name, mapping) -> {
                    mapping.getHandlerMethods().forEach((key, value) -> {
                        System.out.println(
                                AppMessages.BRACKET_OPEN
                                        + name
                                        + AppMessages.BRACKET_CLOSE_SPACE
                                        + key
                                        + AppMessages.ARROW
                                        + value
                        );
                    });
                });
            } catch (Exception e) {
                System.out.println(AppMessages.ENDPOINTS_ERROR_PREFIX + e.getMessage());
            }
            System.out.println(AppMessages.ENDPOINTS_FOOTER);
        };
    }

    private static String maskDbUrl(String url) {
        if (url == null) {
            return AppMessages.MASK_DB_URL_FALLBACK;
        }
        return url.replaceAll(AppMessages.DB_PASSWORD_REGEX, AppMessages.DB_PASSWORD_REPLACEMENT);
    }
}