package service;

import dao.UserDAO;
import model.User;
import server.Request;
import server.Response;
import http.HttpStatus;
import http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;


public class UserService implements server.Service {
    private final UserDAO userDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            String path = request.getPathname();
            String method = request.getMethod().name();
            String auth = request.getAuth();

            // POST /users (Registrierung)
            if ("POST".equals(method) && "/users".equals(path)) {
                User user = objectMapper.readValue(request.getBody(), User.class);
                boolean success = registerUser(user);
                return new Response(
                        success ? HttpStatus.CREATED : HttpStatus.CONFLICT,
                        ContentType.JSON,
                        success ? "User created" : "User exists"
                );
            }

            // GET /users/{username} (Profil abrufen)
            else if ("GET".equals(method) && path.matches("/users")) {
                String token = request.getHeaderMap().getHeader("Authorization");
                if (token != null && token.startsWith("Basic ")) {
                    token = token.substring(6);
                }
                User user = getUserProfile(auth, token);
                return new Response(
                        user != null ? HttpStatus.OK : HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        user != null ? objectMapper.writeValueAsString(user) : "Unauthorized"
                );
            }

            // PUT /users/{username} (Profil aktualisieren)
            else if ("PUT".equals(method) && path.matches("/users")) {
                String token = request.getHeaderMap().getHeader("Authorization");
                if (token != null && token.startsWith("Basic ")) {
                    token = token.substring(6);
                }
                User user = getUserProfile(auth, token);
                if(user == null) {
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized");
                }
                User entry = objectMapper.readValue(request.getBody(), User.class);
                user.updateFrom(entry);
                boolean success = updateUserProfile(user, token);
                return new Response(
                        success ? HttpStatus.OK : HttpStatus.UNAUTHORIZED,
                        ContentType.JSON,
                        success ? "Updated" : "Unauthorized"
                );
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Route not found");
        } catch (Exception e) {
            e.printStackTrace();
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Internal Server Error");
        }
    }

    public boolean registerUser(User user) {
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            return false;
        }
        return userDAO.createUser(user);
    }

    public User getUserProfile(String username, String token) {
        User authenticatedUser = userDAO.getUserByToken(token);
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            return authenticatedUser;
        }
        return null;
    }

    public boolean updateUserProfile(User user, String token) {
        User authenticatedUser = userDAO.getUserByToken(token);
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(user.getUsername())) {
            return userDAO.updateUser(user);
        }
        return false;
    }

    public String loginUser(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = username + "-sebToken";
            userDAO.setUserToken(username, token);
            return token;
        }
        return null;
    }
}