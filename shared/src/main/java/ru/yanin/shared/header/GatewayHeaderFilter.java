package ru.yanin.shared.header;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static ru.yanin.shared.header.HeaderName.PROCESSED;

public class GatewayHeaderFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String uri = request.getRequestURI();

        if (uri.contains("/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }

        String headerValue = request.getHeader(PROCESSED.value());
        if (!Boolean.TRUE.toString().equalsIgnoreCase(headerValue)) {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("text/plain");
            response.getWriter().write("Access denied: Missing or invalid " + PROCESSED.value() + " header");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
