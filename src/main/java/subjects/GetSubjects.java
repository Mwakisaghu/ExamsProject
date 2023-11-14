package subjects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static queries.QueryManager.connection;

public class GetSubjects implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        List<LinkedHashMap<String, Object>> subjects = retrieveSubjectsFromDb();

        // Ensure student is not null
        if(subjects != null) {
            // Convert the result set into a json obj
            String jsonResponse = convertSubjectsListToJson(subjects);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        }
        sendResponse(exchange, 400, "Error : Student cannot be null");
    }
    private List<LinkedHashMap<String, Object>> retrieveSubjectsFromDb() {
        try {
            String selectQuery = "SELECT * FROM subjects";

            return QueryManager.executeSelectQuery(selectQuery, new HashMap<>());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    private String convertSubjectsListToJson(List<LinkedHashMap<String, Object>> students) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register JavaTimeModule for Java 8 date/time support
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.writeValueAsString(students);
    }
    private void sendResponse(HttpServerExchange exchange, int statusCode, String message) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(message);
    }
}
