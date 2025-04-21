import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import dao.HistoryDAO;
import dao.TournamentDAO;
import dao.UserDAO;
import db.DatabaseManager;
import handler.*;
import service.HistoryService;
import service.StatsService;
import service.TournamentService;
import service.UserService;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) throws IOException {
        int port = 10001;
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        // Database + DAOs
        DatabaseManager dbManager = new DatabaseManager();
        UserDAO userDAO = new UserDAO(dbManager);
        HistoryDAO historyDAO = new HistoryDAO(dbManager);
        TournamentDAO tournamentDAO = new TournamentDAO(dbManager);

        // Services
        UserService userService = new UserService(userDAO);
        HistoryService historyService = new HistoryService(historyDAO, userDAO);
        TournamentService tournamentService = new TournamentService(tournamentDAO, userDAO);
        StatsService statsService = new StatsService(userDAO, historyDAO);

        // Handlers
        server.createContext("/users", new UserHandler(userService));
        server.createContext("/sessions", new SessionHandler(userService));
        server.createContext("/history", new HistoryHandler(historyService, tournamentService));
        server.createContext("/stats", new StatsHandler(statsService));
        server.createContext("/score", new ScoreHandler(statsService));
        server.createContext("/tournament", (HttpHandler) new TournamentHandler(tournamentService));

        server.setExecutor(Executors.newFixedThreadPool(10));
        server.start();
        System.out.println("Server running on port " + port);

        // Starte Turnier-Cleanup-Task
        //new Thread(new TournamentCleanupTask(tournamentService)).start();
    }
}