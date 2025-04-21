package service;

import dao.HistoryDAO;
import dao.UserDAO;
import model.Stats;
import model.User;

import java.util.List;
import java.util.stream.Collectors;

public class StatsService {
    private final UserDAO userDAO;
    private final HistoryDAO historyDAO;

    public StatsService(UserDAO userDAO, HistoryDAO historyDAO) {
        this.userDAO = userDAO;
        this.historyDAO = historyDAO;
    }

    public Stats getUserStats(String username) {
        Stats stats = new Stats();
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            stats.setElo(user.getElo());
            stats.setTotalPushups(historyDAO.getTotalPushupsForUser(username));
        }
        return stats;
    }

    public List<Stats> getScoreboard() {
        return userDAO.getAllUsers().stream()
                .map(user -> {
                    Stats stats = new Stats();
                    stats.setElo(user.getElo());
                    stats.setTotalPushups(historyDAO.getTotalPushupsForUser(user.getUsername()));
                    return stats;
                })
                .collect(Collectors.toList());
    }
}