package service;

import dao.HistoryDAO;
import dao.UserDAO;
import model.HistoryEntry;
import model.User;
import java.util.List;

public class HistoryService {
    private final HistoryDAO historyDAO;
    private final UserDAO userDAO;

    public HistoryService(HistoryDAO historyDAO, UserDAO userDAO) {
        this.historyDAO = historyDAO;
        this.userDAO = userDAO;
    }

    public boolean addHistoryEntry(HistoryEntry entry, String token) {
        User user = userDAO.getUserByToken(token);
        if (user == null) {
            return false;
        }

        entry.setUsername(user.getUsername());
        return historyDAO.addHistoryEntry(entry);
    }

    public List<HistoryEntry> getUserHistory(String username, String token) {
        User authenticatedUser = userDAO.getUserByToken(token);
        if (authenticatedUser != null && authenticatedUser.getUsername().equals(username)) {
            return historyDAO.getHistoryForUser(username);
        }
        return null;
    }

    public int getTotalPushupsForUser(String username) {
        return historyDAO.getTotalPushupsForUser(username);
    }
}