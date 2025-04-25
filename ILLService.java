import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

public class LibraryService {
    // Group to (serviceName -> baseUrl) map
    private static final Map<String, Map<String, String>> groups = new HashMap<>();

    public static void main(String[] args) throws IOException {
        int port = 8000; // Default port
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/ping", new PingHandler());
        server.createContext("/register", new RegisterHandler());
        server.createContext("/sendBook", new SendBookHandler());
        server.createContext("/requestBook", new RequestBookHandler());

        server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Server started on port " + port);
        server.start();
    }

    static class PingHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "pong";
            respond(exchange, 200, response);
        }
    }

    static class RegisterHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                // Expect format: group=GROUP&name=SERVICE_NAME&url=BASE_URL
                Map<String, String> params = parseQuery(body);
                String group = params.get("group");
                String name = params.get("name");
                String url = params.get("url");

                if (group != null && name != null && url != null) {
                    groups.computeIfAbsent(group, k -> new HashMap<>()).put(name, url);
                    respond(exchange, 200, "Registered " + name + " in group " + group);
                } else {
                    respond(exchange, 400, "Missing required fields");
                }
            } else {
                respond(exchange, 405, "Method Not Allowed");
            }
        }
    }

    static class SendBookHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            respond(exchange, 200, "sendBook stub hit");
        }
    }

    static class RequestBookHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            respond(exchange, 200, "requestBook stub hit");
        }
    }

    private static void respond(HttpExchange exchange, int status, String body) throws IOException {
        exchange.sendResponseHeaders(status, body.length());
        OutputStream os = exchange.getResponseBody();
        os.write(body.getBytes());
        os.close();
    }

    private static Map<String, String> parseQuery(String query) {
        Map<String, String> map = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] parts = pair.split("=", 2);
            if (parts.length == 2) {
                map.put(parts[0], parts[1]);
            }
        }
        return map;
    }
}