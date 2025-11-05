package com.aerotickets.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro que mide el tiempo de ejecución de cada petición HTTP
 * y registra errores y tiempos lentos con alertas visuales.
 */
@Component
public class PerformanceLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long startTime = System.currentTimeMillis();
        HttpServletRequest req = (HttpServletRequest) request;

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;
        HttpServletResponse res = (HttpServletResponse) response;

        int status = res.getStatus();
        String method = req.getMethod();
        String uri = req.getRequestURI();

        // Colores ANSI para visibilidad
        final String RESET = "\033[0m";
        final String RED = "\033[0;31m";
        final String GREEN = "\033[0;32m";
        final String YELLOW = "\033[0;33m";

        String color;
        if (status >= 500) color = RED; // Error interno
        else if (status >= 400) color = YELLOW; // Error cliente
        else color = GREEN; // OK

        String msg = String.format("%s➡️ %s %s - %d (%d ms)%s",
                color, method, uri, status, duration, RESET);

        if (status >= 500) {
            logger.error(msg);
        } else if (status >= 400) {
            logger.warn(msg);
        } else if (duration > 1000) { // Más de 1 segundo = lento
            logger.warn("⚠️ Petición lenta detectada: {} {} ({} ms)", method, uri, duration);
        } else {
            logger.info(msg);
        }
    }
}