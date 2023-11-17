package grades;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.SQLException;
import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import responses.StatusResponses;

import static queries.QueryManager.connection;

public class GetGrades implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        List <LinkedHashMap<String, Object>> grades = retrieveGradesFromDatabase();

        if (grades != null) {
            // Converting the ResultSet into a JSON
            String jsonResponse = convertGradesListToJson(grades);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Internal server errors
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Failed to retrieve grades data from the database");
        }
    }
    private List<LinkedHashMap<String, Object>> retrieveGradesFromDatabase() {
        try {
            String selectQuery = "SELECT * FROM grades";
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

    private String convertGradesListToJson(List<LinkedHashMap<String, Object>> grades) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register JavaTimeModule for Java 8 date/time support
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.writeValueAsString(grades);
    }
}
