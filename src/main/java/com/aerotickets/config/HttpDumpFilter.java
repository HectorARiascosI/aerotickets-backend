package com.aerotickets.config;

import com.aerotickets.constants.HttpDumpConstants;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class HttpDumpFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(HttpDumpFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        StringBuilder sb = new StringBuilder();

        sb.append(HttpDumpConstants.REQUEST_PREFIX)
                .append(req.getMethod())
                .append(HttpDumpConstants.METHOD_URI_SEPARATOR)
                .append(req.getRequestURI());

        Collections.list(req.getHeaderNames()).forEach(h -> {
            sb.append(System.lineSeparator())
                    .append(HttpDumpConstants.HEADER_LINE_PREFIX)
                    .append(h)
                    .append(HttpDumpConstants.HEADER_SEPARATOR)
                    .append(Collections.list(req.getHeaders(h)));
        });

        log.info(sb.toString());
        chain.doFilter(request, response);
    }
}