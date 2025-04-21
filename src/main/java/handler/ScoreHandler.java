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

public class ScoreHandler implements HttpHandler {
    private final StatsService statsService;
    private final ObjectMapper objectMapper;

    public ScoreHandler(StatsService statsService) {
        this.statsService = statsService;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            // Nur GET-Anfragen erlauben
            if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Method Not Allowed");
                return;
            }

            // Authentifizierung überprüfen
            String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
            if (authHeader == null || !authHeader.startsWith("Basic ")) {
                sendResponse(exchange, 401, "Unauthorized - Missing or invalid Authorization header");
                return;
            }

            // Scoreboard-Daten abrufen
            List<Stats> scoreboard = statsService.getScoreboard();

            // Erfolgreiche Antwort mit Scoreboard-Daten
            String response = objectMapper.writeValueAsString(scoreboard);
            sendResponse(exchange, 200, response);

        } catch (Exception e) {
            e.printStackTrace();
            sendResponse(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
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