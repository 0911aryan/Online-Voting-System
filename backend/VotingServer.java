package backend;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class VotingServer {
    private static PollingSystem pollingSystem = new PollingSystem();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
        server.createContext("/api/candidates", new CandidatesHandler());
        server.createContext("/api/auth", new AuthHandler());
        server.createContext("/api/vote", new VoteHandler());
        server.createContext("/api/audit", new AuditHandler());
        server.setExecutor(null);
        server.start();
        System.out.println("Voting Server running with Bonus Modules on http://localhost:8080");
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(200, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String voterId = "", name = "";
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("voterId=")) voterId = param.split("=")[1];
                    if (param.startsWith("name=")) name = param.split("=")[1].replace("%20", " ");
                }
            }
            String responseMsg = pollingSystem.authenticate(voterId, name);
            sendResponse(exchange, "{\"message\": \"" + responseMsg + "\"}");
        }
    }

    static class CandidatesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < pollingSystem.getCandidates().size(); i++) {
                Candidate c = pollingSystem.getCandidates().get(i);
                json.append(String.format("{\"id\":%d, \"name\":\"%s\", \"post\":\"%s\", \"votes\":%d}", 
                        c.getId(), c.getName(), c.getPost(), c.getVoteCount()));
                if (i < pollingSystem.getCandidates().size() - 1) json.append(",");
            }
            json.append("]");
            sendResponse(exchange, json.toString());
        }
    }

    static class VoteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String voterId = "";
            int candidateId = -1;
            if (query != null) {
                for (String param : query.split("&")) {
                    if (param.startsWith("voterId=")) voterId = param.split("=")[1];
                    if (param.startsWith("candidateId=")) candidateId = Integer.parseInt(param.split("=")[1]);
                }
            }
            String responseMsg = pollingSystem.castVote(voterId, candidateId);
            sendResponse(exchange, "{\"message\": \"" + responseMsg + "\"}");
        }
    }

    static class AuditHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < pollingSystem.getAuditLog().size(); i++) {
                json.append("\"").append(pollingSystem.getAuditLog().get(i)).append("\"");
                if (i < pollingSystem.getAuditLog().size() - 1) json.append(",");
            }
            json.append("]");
            sendResponse(exchange, json.toString());
        }
    }
}