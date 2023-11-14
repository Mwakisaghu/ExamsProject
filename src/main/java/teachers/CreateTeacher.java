package teachers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;

import static queries.QueryManager.connection;

public class CreateTeacher implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {
        // Extract the request body
        String reqBody = RestUtils.getRequestBody(exchange);

        Gson gson = new Gson();
        HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

        if (requestBodyMap == null) {
            // Bad Request
            sendResponse(exchange, 400, "Error: Invalid request - request body not found");
            return;
        }

                // Define the SQL INSERT query
                String insertQuery = "INSERT INTO teachers (teacher_id, tsc_number, first_name, middle_name, last_name, gender, title, home_address, city, state, mobile_number, email_address, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

                // Prepare the parameters for the INSERT operation
                HashMap<Integer, Object> insertMap = new HashMap<>();
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
                QueryManager.executeSelectQuery(insertQuery, insertMap);

                sendResponse(exchange, 201, "Teacher Created Successfully");

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

    private void sendResponse(HttpServerExchange exchange, int statusCode, String message) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(message);
    }
}
