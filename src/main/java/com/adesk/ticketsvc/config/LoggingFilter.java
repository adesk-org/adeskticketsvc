package com.adesk.ticketsvc.config;

import java.io.IOException;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        if (request instanceof HttpServletRequest req
                && response instanceof HttpServletResponse res) {
            long start = System.currentTimeMillis();
            log.debug("Received request: {} {}", req.getMethod(), req.getRequestURI());
            try {
                chain.doFilter(request, response);
            } finally {
                long duration = System.currentTimeMillis() - start;
                log.info("Served {} response in {} ms: {} {}", res.getStatus(), duration,
                        req.getMethod(), req.getRequestURI());
            }
        } else {
            chain.doFilter(request, response);
        }

    }
}
