package service;

import dao.UserDAO;
import model.User;

import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public boolean registerUser(User user) {
        if (userDAO.getUserByUsername(user.getUsername()) != null) {
            return false; // User already exists
        }
        return userDAO.createUser(user);
    }

    public String loginUser(String username, String password) {
        User user = userDAO.getUserByUsername(username);
        if (user != null && user.getPassword().equals(password)) {
            String token = username + "-sebToken"; // Simple token as in CURL tests
            userDAO.setUserToken(username, token);
            return token;
        }
        return null;
    }

    public User getUserProfile(String username, String token) {
        User user = userDAO.getUserByToken(token);
        if (user != null && user.getUsername().equals(username)) {
            return user;
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

    private String generateToken(String username) {
        return username + "-sebToken-" + UUID.randomUUID().toString().substring(0, 8);
    }
}