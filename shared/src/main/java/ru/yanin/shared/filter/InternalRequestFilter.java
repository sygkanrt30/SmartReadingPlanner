package ru.yanin.shared.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static ru.yanin.shared.filter.HeaderName.INTERNAL_CODE;

@RequiredArgsConstructor
@Slf4j
public class InternalRequestFilter extends OncePerRequestFilter {

    private final String internalCode;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        if (!uri.contains("/internal/")) {
            filterChain.doFilter(request, response);
            return;
        }
        String headerValue = request.getHeader(INTERNAL_CODE.value());
        if (!internalCode.equalsIgnoreCase(headerValue)) {
            log.error("Invalid internal request header");
            ResponseEnricher.enrich(response, INTERNAL_CODE);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
