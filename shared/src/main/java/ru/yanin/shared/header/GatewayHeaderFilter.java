package ru.yanin.shared.header;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

import static ru.yanin.shared.header.HeaderName.PROCESSED;

public class GatewayHeaderFilter extends OncePerRequestFilter {

    private final List<RequestMatcher> skipPathMatchers;

    public GatewayHeaderFilter() {
        skipPathMatchers = List.of(
                PathPatternRequestMatcher.withDefaults().matcher("/*/*internal*/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/*internal*/**"),
                PathPatternRequestMatcher.withDefaults().matcher("/actuator/**")
        );
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        if (shouldSkip(request)) {
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

    private boolean shouldSkip(HttpServletRequest request) {
        for (RequestMatcher matcher : skipPathMatchers) {
            if (matcher.matches(request))
                return true;
        }
        return false;
    }
}
