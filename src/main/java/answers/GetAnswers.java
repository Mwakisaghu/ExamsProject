package answers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

import static queries.QueryManager.connection;

public class GetAnswers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Retrieve sorting and filtering parameters from query string
        HashMap<String, String> queryParams = RestUtils.getQueryParams(exchange, "sort", "filter");
        String sortBy = queryParams.getOrDefault("sort", "");
        String filterBy = queryParams.getOrDefault("filter", "");

        // Retrieve answers from the database based on sorting and filtering
        List<LinkedHashMap<String, Object>> answers = retrieveAnswersFromDatabase(sortBy, filterBy);

        if (answers != null) {
            // Convert the ResultSet into a JSON
            String jsonResponse = convertAnswersListToJson(answers);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Internal server errors
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Failed to retrieve answers data from the database");
        }
    }

    private List<LinkedHashMap<String, Object>> retrieveAnswersFromDatabase(String sortBy, String filterBy) {
        try {
            // SQL query to retrieve answers
            StringBuilder selectQuery = new StringBuilder("SELECT * FROM answers");

            // Applying filtering if necessary
            if (!filterBy.isEmpty()) {
                selectQuery.append(" WHERE ").append(filterBy);
            }

            // Applying sorting if necessary
            if (!sortBy.isEmpty()) {
                selectQuery.append(" ORDER BY ").append(sortBy);
            }

            // Check if both sorting and filtering criteria are empty
            if (filterBy.isEmpty() && sortBy.isEmpty()) {
                // No sorting or filtering criteria provided, retrieve all answers
                return QueryManager.executeSelectQuery(selectQuery.toString(), new HashMap<>());
            } else {
                // Sorting or filtering criteria provided, execute the SQL query
                return QueryManager.executeSelectQuery(selectQuery.toString(), new HashMap<>());
            }
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

    private String convertAnswersListToJson(List<LinkedHashMap<String, Object>> answers) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        // Register JavaTimeModule for Java 8 date/time support
        objectMapper.registerModule(new JavaTimeModule());

        return objectMapper.writeValueAsString(answers);
    }
}
