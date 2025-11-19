package com.aerotickets.config;

import com.aerotickets.constants.RequestLoggingConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RequestLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        long startTime = System.currentTimeMillis();
        HttpServletRequest req = (HttpServletRequest) request;
        String method = req.getMethod();
        String uri = req.getRequestURI();

        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;

        logger.info(RequestLoggingConstants.LOG_PATTERN_REQUEST_COMPLETED, method, uri, duration);
    }
}