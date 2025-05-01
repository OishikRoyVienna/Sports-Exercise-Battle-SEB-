package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.HistoryEntry;
import model.Tournament;
import service.HistoryService;
import service.TournamentService;
import db.DatabaseManager;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class HistoryHandler implements HttpHandler {
    private final HistoryService historyService;
    private final TournamentService tournamentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HistoryHandler(HistoryService historyService, TournamentService tournamentService) {
        this.historyService = historyService;
        this.tournamentService = tournamentService;
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();

            if ("POST".equalsIgnoreCase(method) && path.equals("/history")) {
                // Add history entry
                String token = getTokenFromHeader(exchange);
                InputStream is = exchange.getRequestBody();
                HistoryEntry entry = objectMapper.readValue(is, HistoryEntry.class);

                boolean success = historyService.addHistoryEntry(entry, token);
                if (success) {
                    // Also add to tournament if it's pushups
                    if ("PushUps".equalsIgnoreCase(entry.getExerciseName())) {
                        tournamentService.startOrJoinTournament(entry.getUsername(), entry.getCount());
                    }
                    sendResponse(exchange, 201, "Entry added");
                } else {
                    sendResponse(exchange, 401, "Unauthorized");
                }
            }
            else if ("GET".equalsIgnoreCase(method) && path.equals("/history")) {
                // Get user history
                String token = getTokenFromHeader(exchange);
                String username = getUsernameFromToken(token);

                if (username != null) {
                    List<HistoryEntry> history = historyService.getUserHistory(username, token);
                    if (history != null) {
                        String response = objectMapper.writeValueAsString(history);
                        sendResponse(exchange, 200, response);
                    } else {
                        sendResponse(exchange, 404, "Not found");
                    }
                } else {
                    sendResponse(exchange, 401, "Unauthorized");
                }
            }
            else {
                sendResponse(exchange, 405, "Method Not Allowed");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        if (token != null && token.contains("-sebToken")) {
            return token.split("-sebToken")[0];
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
    private boolean isAuthorized(HttpExchange exchange, String username) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader == null || !authHeader.startsWith("Basic ")) {
            return false;
        }
        String token = authHeader.substring(6);
        return token.equals(username + "-sebToken");
    }
}