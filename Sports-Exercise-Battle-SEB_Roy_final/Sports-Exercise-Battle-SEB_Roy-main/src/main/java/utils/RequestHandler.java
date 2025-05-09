package utils;

import http.ContentType;
import http.HttpStatus;
import server.Request;
import server.Response;
import server.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class RequestHandler implements Runnable {
    private Socket clientSocket;
    private Router router;
    private PrintWriter printWriter;
    private BufferedReader bufferedReader;

    public RequestHandler(Socket clientSocket, Router router) throws IOException {
        this.clientSocket = clientSocket;
        this.bufferedReader = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));
        this.printWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
        this.router = router;
    }

    @Override
    public void run() {
        try {
            Request request = new RequestBuilder().buildRequest(this.bufferedReader);

            if (request.getPathname() == null) {
                sendErrorResponse(HttpStatus.BAD_REQUEST, "Invalid request");
                return;
            }
            String auth = "";
            String pathname = request.getPathname();

            if (pathname.startsWith("/users/")) {
                auth = pathname.substring("/users/".length());
                pathname = "/users";
            }
            request.setPathname(pathname);
            request.setAuth(auth);

            Service service = router.resolve(request.getMethod().name(), request.getPathname(), auth);
            if (service == null) {
                sendErrorResponse(HttpStatus.NOT_FOUND, "Route not found");
                return;
            }

            Response response = service.handleRequest(request);
            printWriter.write(response.get());
            printWriter.flush();

        } catch (IOException e) {
            System.err.println("Error handling request: " + e.getMessage());
        } finally {
            try {
                if (printWriter != null) printWriter.close();
                if (bufferedReader != null) bufferedReader.close();
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void sendErrorResponse(HttpStatus status, String message) throws IOException {
        Response response = new Response(status, ContentType.PLAIN_TEXT, message);
        printWriter.write(response.get());
        printWriter.flush();
    }
}