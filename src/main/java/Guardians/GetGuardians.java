package Guardians;

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

public class GetGuardians implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        List <LinkedHashMap<String, Object>> guardians = retrieveGuardiansFromDatabase();


        if (guardians != null) {
            // Converting the ResultSet into a JSON
            String jsonResponse = convertGuardiansListToJson(guardians);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Internal server errors
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Failed to retrieve exams data from the database");
        }
    }
    private List<LinkedHashMap<String, Object>> retrieveGuardiansFromDatabase() {
        try {
            String selectQuery = "SELECT * FROM guardian";
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

    private String convertGuardiansListToJson(List<LinkedHashMap<String, Object>> guardians) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register JavaTimeModule for Java 8 date/time support
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.writeValueAsString(guardians);
    }
}
