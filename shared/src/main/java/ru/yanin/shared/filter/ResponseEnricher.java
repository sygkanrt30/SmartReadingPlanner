package ru.yanin.shared.filter;

import jakarta.servlet.http.HttpServletResponse;
import lombok.experimental.UtilityClass;

import java.io.IOException;

@UtilityClass
class ResponseEnricher {

    void enrich(HttpServletResponse resp, HeaderName headerName) throws IOException {
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.setContentType("text/plain");
        resp.getWriter().write("Access denied: Missing or invalid " + headerName.value() + " header");
    }
}
