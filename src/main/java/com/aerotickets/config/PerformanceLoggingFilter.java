package com.aerotickets.config;

import com.aerotickets.constants.PerformanceLoggingConstants;
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

        String color;
        if (status >= 500) {
            color = PerformanceLoggingConstants.ANSI_RED;
        } else if (status >= 400) {
            color = PerformanceLoggingConstants.ANSI_YELLOW;
        } else {
            color = PerformanceLoggingConstants.ANSI_GREEN;
        }

        String msg = String.format(
                PerformanceLoggingConstants.LOG_PATTERN_REQUEST,
                color,
                method,
                uri,
                status,
                duration,
                PerformanceLoggingConstants.ANSI_RESET
        );

        if (status >= 500) {
            logger.error(msg);
        } else if (status >= 400) {
            logger.warn(msg);
        } else if (duration > PerformanceLoggingConstants.SLOW_REQUEST_THRESHOLD_MS) {
            logger.warn(PerformanceLoggingConstants.LOG_SLOW_REQUEST, method, uri, duration);
        } else {
            logger.info(msg);
        }
    }
}
