package teachers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CreateTeacher implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        // Extract the request body
        String reqBody = RestUtils.getRequestBody(exchange);

        Gson gson = new Gson();
        HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

        // Check for the presence of a valid request body
        if (requestBodyMap == null) {
            // Bad Request
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Error: Invalid request - request body not found");
            return;
        }

        // Define the SQL INSERT query
        String insertQuery = "INSERT INTO teachers (teacher_id, tsc_number, first_name, middle_name, last_name, gender, title, home_address, city, state, mobile_number, email_address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        // Prepare the parameters for the INSERT operation
        Map<Integer, Object> insertMap = new HashMap<>();
        insertMap.put(1, requestBodyMap.get("teacher_id"));
        insertMap.put(2, requestBodyMap.get("tsc_number"));
        insertMap.put(3, requestBodyMap.get("first_name"));
        insertMap.put(4, requestBodyMap.get("middle_name"));
        insertMap.put(5, requestBodyMap.get("last_name"));
        insertMap.put(6, requestBodyMap.get("gender"));
        insertMap.put(7, requestBodyMap.get("title"));
        insertMap.put(8, requestBodyMap.get("home_address"));
        insertMap.put(9, requestBodyMap.get("city"));
        insertMap.put(10, requestBodyMap.get("state"));
        insertMap.put(11, requestBodyMap.get("mobile_number"));
        insertMap.put(12, requestBodyMap.get("email_address"));

        try {
            // Execute the SQL INSERT query using the QueryManager
            QueryManager.executeInsertQuery(insertQuery, insertMap);

            // Created
            exchange.setStatusCode(201);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Teacher created successfully");
        } catch (SQLException e) {
            e.printStackTrace();
            // Internal Server Error
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Error: Internal Server Error");
        }
    }
}
