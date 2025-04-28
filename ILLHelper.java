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

public class ILLHelper implements Runnable {
    public void run() {
        int port = 8001; // Default port
        try{        
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/ping", new PingHandler());
        server.createContext("/isAvailable", new IsAvailableHandler());
        server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Server started on port " + port);
        server.start();
        } catch(Exception e){
            System.out.println(e.toString());
        }
    }

    static class PingHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            String response = "pong";
            respond(exchange, 200, response);
        }
    }

    static class IsAvailableHandler implements HttpHandler {
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                String body = new String(exchange.getRequestBody().readAllBytes());
                // Expect format: isbn=ISBN&author=LIBRARY_NAME&url=BASE_URL
                Map<String, String> params = parseQuery(body);
                String ISBN = params.get("isbn");
                String author = params.get("author");
                String title = params.get("title");

                if (ISBN != null && author != null && title != null) {
                    boolean isAvailable = true /*TODO Replace with your isAvailable*/;
                    if(true /*Check Availability*/){
                        respond(exchange, 200, "Resource is available.");
                    } else {
                        respond(exchange, 404, "Resource not available.");
                    }
                } else {
                    respond(exchange, 400, "Missing required fields");
                }
            } else {
                respond(exchange, 405, "Method Not Allowed");
            }
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