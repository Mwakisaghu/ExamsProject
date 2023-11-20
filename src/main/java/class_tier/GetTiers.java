package class_tier;

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

public class GetTiers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        List <LinkedHashMap<String, Object>> tiers = retrieveTiersFromDatabase();


        if (tiers != null) {
            // Converting the ResultSet into a JSON
            String jsonResponse = convertTiersListToJson(tiers);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Internal server errors
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Failed to retrieve class tiers data from the database");
        }
    }
    private List<LinkedHashMap<String, Object>> retrieveTiersFromDatabase() {
        try {
            String selectQuery = "SELECT * FROM class_tier";
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

    private String convertTiersListToJson(List<LinkedHashMap<String, Object>> tiers) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register JavaTimeModule for Java 8 date/time support
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.writeValueAsString(tiers);
    }
}
