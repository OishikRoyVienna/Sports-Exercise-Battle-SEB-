package service;

import dao.UserDAO;
import dao.HistoryDAO;
import model.Stats;
import model.User;
import server.Request;
import server.Response;
import http.HttpStatus;
import http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Collectors;

public class StatsService implements server.Service {
    private final UserDAO userDAO;
    private final HistoryDAO historyDAO;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public StatsService(UserDAO userDAO, HistoryDAO historyDAO) {
        this.userDAO = userDAO;
        this.historyDAO = historyDAO;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            String path = request.getPathname();

            if ("/stats".equals(path)) {
                String token = request.getHeaderMap().getHeader("Authorization");
                if (token != null && token.startsWith("Basic ")) {
                    token = token.substring(6);
                }
                String username = token != null ? token.replace("-sebToken", "") : null;

                if (username == null) {
                    return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized");
                }

                Stats stats = getUserStats(username);
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        objectMapper.writeValueAsString(stats)
                );
            } else if ("/score".equals(path)) {
                List<Stats> scoreboard = getScoreboard();
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        objectMapper.writeValueAsString(scoreboard)
                );
            }

            return new Response(HttpStatus.NOT_FOUND, ContentType.PLAIN_TEXT, "Not Found");
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Internal Server Error");
        }
    }

    public Stats getUserStats(String username) {
        Stats stats = new Stats();
        User user = userDAO.getUserByUsername(username);
        if (user != null) {
            int totalPushups = historyDAO.getTotalPushupsForUser(username);
            updateBadges(user, totalPushups);
            stats.setElo(user.getElo());
            stats.setTotalPushups(totalPushups);
            stats.setBadges(user.getBadges());
        }
        return stats;
    }

    public List<Stats> getScoreboard() {
        return userDAO.getAllUsers().stream()
                .map(user -> {
                    Stats stats = new Stats();
                    stats.setElo(user.getElo());
                    stats.setTotalPushups(historyDAO.getTotalPushupsForUser(user.getUsername()));
                    stats.setBadges(user.getBadges());
                    return stats;
                })
                .collect(Collectors.toList());
    }

    private void updateBadges(User user, int totalPushups) {
        if (totalPushups >= 150) user.addBadge("Hulk");
        if (totalPushups >= 100) user.addBadge("Soldier");
        if (totalPushups >= 50) user.addBadge("Fighter");
        if (user.getElo() >= 80) user.addBadge("ELO Warrior");
        if (user.getElo() >= 120) user.addBadge("ELO Champ");
        if (user.getElo() >= 150) user.addBadge("ELO Maestro");
    }
}