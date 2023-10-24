package students;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;


import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CreateStudent implements HttpHandler {
    private final Connection connection;

    public CreateStudent(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extract student information from the request body
        Map<String, Object> requestBodyMap = parseReqBody(exchange);

        // Checking for the requestBody
        if (requestBodyMap == null) {
            // Bad Request
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error : Invalid request - request body not found");
        }

        // Sql Query
        String insertSql = "INSERT INTO students (student_id, first_name, middle_name, last_name, class_tier_id, date_of_birth, gender, email_address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        // Preparing the parameters for the insert operation
        Map<Integer, Object> insertMap = new HashMap<>();
        assert requestBodyMap != null;
        insertMap.put(1, requestBodyMap.get("student_id"));
        insertMap.put(2, requestBodyMap.get("first_name"));
        insertMap.put(3, requestBodyMap.get("middle_name"));
        insertMap.put(4, requestBodyMap.get("last_name"));
        insertMap.put(5, requestBodyMap.get("class_tier_id"));
        insertMap.put(6, requestBodyMap.get("date_of_birth"));
        insertMap.put(7, requestBodyMap.get("gender"));
        insertMap.put(8, requestBodyMap.get("email_address"));

        try {
            // Executing the insert query & getting the generated keys and row count
            QueryManager.executeInsertQuery(insertSql, insertMap);

            // Status - Created
            exchange.setStatusCode(201);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Student created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            // Status - Internal Server Error
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Internal Server Error");
        }
    }

    // Parsing Json Request Body
    private Map<String, Object> parseReqBody(HttpServerExchange exchange) throws IOException {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            byte[] reqBodyBytes = exchange.getInputStream().readAllBytes();
            return objectMapper.readValue(reqBodyBytes, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
