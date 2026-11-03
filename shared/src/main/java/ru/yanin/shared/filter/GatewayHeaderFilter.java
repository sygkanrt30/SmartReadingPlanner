package ru.yanin.shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static ru.yanin.shared.filter.HeaderName.PROCESSED;

@Slf4j
public class GatewayHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (uri.contains("/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String headerValue = request.getHeader(PROCESSED.value());
        if (!Boolean.TRUE.toString().equalsIgnoreCase(headerValue)) {
            log.error("Invalid gateway processed header");
            ResponseEnricher.enrich(response, PROCESSED);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
