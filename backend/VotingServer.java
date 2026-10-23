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

        // API Endpoints
        server.createContext("/api/candidates", new CandidatesHandler());
        server.createContext("/api/vote", new VoteHandler());

        server.setExecutor(null);
        server.start();
        System.out.println("Voting Server is running on http://localhost:8080");
    }

    private static void sendResponse(HttpExchange exchange, String response) throws IOException {
        // Allows our local HTML file to connect without CORS errors
        exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    // Endpoint to get the list of candidates and their live vote counts
    static class CandidatesHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < pollingSystem.getCandidates().size(); i++) {
                Candidate c = pollingSystem.getCandidates().get(i);
                json.append(String.format("{\"id\":%d, \"name\":\"%s\", \"votes\":%d}", c.getId(), c.getName(), c.getVoteCount()));
                if (i < pollingSystem.getCandidates().size() - 1) json.append(",");
            }
            json.append("]");
            sendResponse(exchange, json.toString());
        }
    }

    // Endpoint to process a vote submission
    static class VoteHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String query = exchange.getRequestURI().getQuery();
            String responseMsg = "Error: Invalid Request";

            if (query != null) {
                String[] params = query.split("&");
                String voterId = "";
                int candidateId = -1;

                // Parse the voter ID and candidate ID from the URL
                for (String param : params) {
                    if (param.startsWith("voterId=")) voterId = param.split("=")[1];
                    if (param.startsWith("candidateId=")) candidateId = Integer.parseInt(param.split("=")[1]);
                }

                // Call the secure PollingSystem logic
                responseMsg = pollingSystem.castVote(voterId, candidateId);
            }

            // Return the success or error message to the frontend
            String jsonResponse = "{\"message\": \"" + responseMsg + "\"}";
            sendResponse(exchange, jsonResponse);
        }
    }
}