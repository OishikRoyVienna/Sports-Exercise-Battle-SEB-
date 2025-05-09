package service;

import dao.HistoryDAO;
import model.HistoryEntry;
import server.Request;
import server.Response;
import http.HttpStatus;
import http.ContentType;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

public class HistoryService implements server.Service {
    private final HistoryDAO historyDAO;
    private final TournamentService tournamentService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public HistoryService(HistoryDAO historyDAO, TournamentService tournamentService) {
        this.historyDAO = historyDAO;
        this.tournamentService = tournamentService;
    }

    @Override
    public Response handleRequest(Request request) {
        try {
            String method = request.getMethod().name();
            String path = request.getPathname();

            // Authentifizierung
            String token = request.getHeaderMap().getHeader("Authorization");
            if (token != null && token.startsWith("Basic ")) {
                token = token.substring(6);
            }
            String username = token != null ? token.replace("-sebToken", "") : null;

            if (username == null) {
                return new Response(HttpStatus.UNAUTHORIZED, ContentType.PLAIN_TEXT, "Unauthorized");
            }

            if ("POST".equalsIgnoreCase(method) && "/history".equals(path)) {
                HistoryEntry entry = objectMapper.readValue(request.getBody(), HistoryEntry.class);
                entry.setUsername(username);
                boolean success = addHistoryEntry(entry);

                if (success && "PushUps".equalsIgnoreCase(entry.getExerciseName())) {
                    tournamentService.addToTournament(username, entry.getCount());
                }

                return new Response(
                        success ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST,
                        ContentType.PLAIN_TEXT,
                        success ? "Entry added" : "Failed to add entry"
                );
            }
            else if ("GET".equalsIgnoreCase(method) && "/history".equals(path)) {
                List<HistoryEntry> history = getHistoryForUser(username);
                return new Response(
                        HttpStatus.OK,
                        ContentType.JSON,
                        objectMapper.writeValueAsString(history)
                );
            }

            return null;
        } catch (Exception e) {
            return new Response(HttpStatus.INTERNAL_SERVER_ERROR, ContentType.PLAIN_TEXT, "Internal Server Error");
        }
    }

    public boolean addHistoryEntry(HistoryEntry entry) {
        return historyDAO.addHistoryEntry(entry);
    }

    public List<HistoryEntry> getHistoryForUser(String username) {
        return historyDAO.getHistoryForUser(username);
    }

    public int getTotalPushupsForUser(String username) {
        return historyDAO.getTotalPushupsForUser(username);
    }
}