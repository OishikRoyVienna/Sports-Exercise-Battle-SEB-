package utils;

import server.Service;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Router {
    private final Map<String, Service> serviceRegistry = new ConcurrentHashMap<>();

    public synchronized void addService(String route, Service service) {
        this.serviceRegistry.put(route, service);
    }

    public synchronized Service resolve(String method, String path, String auth) {
        if (path.endsWith("/" + auth)) {
            path = path.substring(0, path.length() - auth.length() - 1);
        }
        String route = method + " " + path;
        return this.serviceRegistry.get(route);
    }
}