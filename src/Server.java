import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class Server {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/applications", Server::handle);
        server.setExecutor(null);
        server.start();
        System.out.println("Server running at http://localhost:8080");
    }

    private static void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            if (method.equals("OPTIONS")) { exchange.sendResponseHeaders(204, -1); return; }

            if (method.equals("GET") && path.equals("/api/applications")) {
                send(exchange, 200, Json.toJsonArray(Database.getAll()));

            } else if (method.equals("POST") && path.equals("/api/applications")) {
                Map<String, String> d = Json.parseFlatObject(readBody(exchange));
                Application app = new Application(0, d.get("companyName"), d.get("role"),
                    d.getOrDefault("status", "Applied"), d.get("appliedDate"), d.get("followUpDate"), d.get("notes"));
                int newId = Database.insert(app);
                send(exchange, 201, "{\"id\":" + newId + "}");

            } else if (method.equals("PUT") && path.matches("/api/applications/\\d+")) {
                Map<String, String> d = Json.parseFlatObject(readBody(exchange));
                boolean ok = Database.updateStatus(extractId(path), d.get("status"));
                send(exchange, ok ? 200 : 404, "{\"updated\":" + ok + "}");

            } else if (method.equals("DELETE") && path.matches("/api/applications/\\d+")) {
                boolean ok = Database.delete(extractId(path));
                send(exchange, ok ? 200 : 404, "{\"deleted\":" + ok + "}");

            } else {
                send(exchange, 404, "{\"error\":\"Not found\"}");
            }
        } catch (Exception e) {
            e.printStackTrace();
            send(exchange, 500, "{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    private static int extractId(String path) {
        String[] parts = path.split("/");
        return Integer.parseInt(parts[parts.length - 1]);
    }

    private static String readBody(HttpExchange exchange) throws IOException {
        return new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static void send(HttpExchange exchange, int status, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) { os.write(bytes); }
    }
}