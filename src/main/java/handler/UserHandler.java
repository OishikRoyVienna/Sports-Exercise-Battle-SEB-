package handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import model.User;
import service.UserService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class UserHandler implements HttpHandler {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            if ("POST".equalsIgnoreCase(method) && pathParts.length == 2) {
                // Register user
                InputStream is = exchange.getRequestBody();
                User user = objectMapper.readValue(is, User.class);
                boolean success = userService.registerUser(user);

                sendResponse(exchange, success ? 201 : 409,
                        success ? "User created" : "User already exists");
            }
            else if ("GET".equalsIgnoreCase(method) && pathParts.length == 3) {
                // Get user profile
                String username = pathParts[2];
                String token = getTokenFromHeader(exchange);

                User user = userService.getUserProfile(username, token);
                if (user != null) {
                    String response = objectMapper.writeValueAsString(user);
                    sendResponse(exchange, 200, response);
                } else {
                    sendResponse(exchange, 401, "Unauthorized");
                }
            }
            else if ("PUT".equalsIgnoreCase(method) && pathParts.length == 3) {
                // Update user profile
                String username = pathParts[2];
                String token = getTokenFromHeader(exchange);
                InputStream is = exchange.getRequestBody();
                User user = objectMapper.readValue(is, User.class);
                user.setUsername(username); // Ensure username matches path

                boolean success = userService.updateUserProfile(user, token);
                sendResponse(exchange, success ? 200 : 401,
                        success ? "Profile updated" : "Unauthorized");
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