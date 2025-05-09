import db.DatabaseManager;
import dao.*;
import service.*;
import server.Server;
import utils.Router;

public class Main {
    public static void main(String[] args) {
        // 1. Database initialisieren (Connection wird hergestellt, Tabellen werden befüllt (Leer) und Tabellen werden gelöscht (falls Daten existieren)
        DatabaseManager dbManager = new DatabaseManager();

        // 2. DAOs erstellen (Ist für die Kommunikation von Service und Datenbank zuständig. DAO Klassen kommunizieren direkt mit der Datenbank. Sie werden von den Services aufgerufen.
        UserDAO userDAO = new UserDAO(dbManager);
        HistoryDAO historyDAO = new HistoryDAO(dbManager);
        TournamentDAO tournamentDAO = new TournamentDAO(dbManager);

        // 3. Services (Sind dafür da um bei Schritt 4 das jeweilige Service anzusprechen)
        UserService userService = new UserService(userDAO);
        SessionService sessionService = new SessionService(userService);
        TournamentService tournamentService = new TournamentService(tournamentDAO, userDAO);
        StatsService statsService = new StatsService(userDAO, historyDAO);
        HistoryService historyService = new HistoryService(historyDAO, tournamentService);

        // 4. Router (Empfängt die Routen vom CURL-Script und ruft je nach empfangene Route das jeweilige benötigte Service auf)
        Router router = new Router();
        router.addService("POST /users", userService);
        router.addService("POST /sessions", sessionService);
        router.addService("GET /users", userService);
        router.addService("PUT /users", userService);
        router.addService("POST /history", historyService);
        router.addService("GET /history", historyService);
        router.addService("GET /stats", statsService);
        router.addService("GET /score", statsService);
        router.addService("GET /tournament", tournamentService);

        // 5. Server starten (Startet den Server um die Kommunikation zwischen Client (CURL-Skript) und Services & Datenbank zu ermöglichen)
        Server server = new Server(10001, router);
        try {
            server.start();
            System.out.println("Server running on port 10001");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}