package com.aerotickets.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1) // muy arriba, después de CORS si lo tuvieras
public class HttpDumpFilter implements Filter {
  private static final Logger log = LoggerFactory.getLogger(HttpDumpFilter.class);

  @Override public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    StringBuilder sb = new StringBuilder();
    sb.append("⟶ ").append(req.getMethod()).append(" ").append(req.getRequestURI());

    Collections.list(req.getHeaderNames()).forEach(h -> {
      sb.append("\n    ").append(h).append(": ").append(Collections.list(req.getHeaders(h)));
    });

    log.info(sb.toString());
    chain.doFilter(request, response);
  }
}