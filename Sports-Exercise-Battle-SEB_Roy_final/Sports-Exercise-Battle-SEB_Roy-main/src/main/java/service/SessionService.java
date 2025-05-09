package service;

import dao.UserDAO;
import server.Request;
import server.Response;
import http.HttpStatus;
import http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import java.util.HashMap;

public class SessionService implements server.Service {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SessionService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            if ("POST".equals(request.getMethod().name()) && "/sessions".equals(request.getPathname())) {
                Map<String, String> credentials = objectMapper.readValue(
                        request.getBody(),
                        HashMap.class
                );

                String username = credentials.get("Username");
                String password = credentials.get("Password");
                String token = userService.loginUser(username, password);

                if (token != null) {
                    Map<String, String> response = new HashMap<>();
                    response.put("Token", token);
                    return new Response(
                            HttpStatus.OK,
                            ContentType.JSON,
                            objectMapper.writeValueAsString(response)
                    );
                } else {
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Invalid credentials");
                }
            }
            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Route not found");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Internal Server Error");
        }
    }
}