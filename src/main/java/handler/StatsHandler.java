package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.Stats;
import service.StatsService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class StatsHandler implements HttpHandler {
    private final StatsService statsService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatsHandler(StatsService statsService) {
        this.statsService = statsService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String token = exchange.getRequestHeaders().getFirst("Authorization").substring(6);
            String username = token.split("-")[0]; // Extract username from token

            Stats stats = statsService.getUserStats(username);
            String response = objectMapper.writeValueAsString(stats);
            sendResponse(exchange, 200, response);
        } catch (Exception e) {
            sendResponse(exchange, 500, "Internal Server Error");
        }
    }


    private String getTokenFromHeader(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Basic ")) {
            return authHeader.substring(6);
        }
        return null;
    }

    private String getUsernameFromToken(String token) {
        if (token != null && token.contains("-sebToken-")) {
            return token.split("-sebToken-")[0];
        }
        return null;
    }

    private void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(statusCode, response.getBytes(StandardCharsets.UTF_8).length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes(StandardCharsets.UTF_8));
        }
    }
    private boolean validateToken(HttpExchange exchange, String expectedUsername) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }
        String token = authHeader.substring(6);
        return token.equals(expectedUsername + "-sebToken");
    }

}