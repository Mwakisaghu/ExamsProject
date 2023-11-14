package teachers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;
import responses.StatusResponses;

import java.sql.SQLException;
import java.util.*;

import static queries.QueryManager.connection;

public class GetTeachers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        try {
            List<LinkedHashMap<String, Object>> teachers = retrieveTeachersFromDatabase();

            if (teachers != null) {
                // Converting the list of teachers into JSON
                String jsonResponse = convertTeachersListToJson(teachers);

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(jsonResponse);
            } else {
                StatusResponses.sendErrorResponse(exchange, "Error: Failed to retrieve teacher data from the database");
            }
        } catch (Exception e) {
            // Handle exceptions here
            e.printStackTrace();

            // Internal server response
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Internal server error");
        }
    }

    private List<LinkedHashMap<String, Object>> retrieveTeachersFromDatabase() {
        try {
            String selectQuery = "SELECT * FROM teachers";
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

    private String convertTeachersListToJson(List<LinkedHashMap<String, Object>> teachers) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());  // Register JavaTimeModule for Java 8 date/time support

        return objectMapper.writeValueAsString(teachers);
    }
}
