package students;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

public class DeleteStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the student ID from the request
        Map<String, Object> requestBodyMap = parseRequestBody(exchange);

        if (requestBodyMap == null) {
            // Checking if the studentId is missing & send Bad Request
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: The studentId is missing");
            return;
        }

        // Extracting the studentId from the request body
        String studentId = (String) requestBodyMap.get("student_id");

        // Sql query
        String deleteQuery = "DELETE FROM students WHERE student_id = ?";

        // Preparing the parameters for the delete operation
        Map<Integer, Object> deleteMap = new HashMap<>();
        deleteMap.put(1, studentId);

        try {
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            // Delete the student's information
            if (rowsDeleted > 0) {
                // success response status -  code 204 (No Content)
                exchange.setStatusCode(204);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Success: Student deleted successfully");
            } else {
                // ID was not found
                exchange.setStatusCode(404);
                exchange.getResponseSender().send("Error: Student not found");
            }
        } catch (NumberFormatException e) {
            // invalid student ID format - Bad Request Response
            exchange.setStatusCode(400);
            exchange.getResponseSender().send("Error: Invalid studentId parameter");
        } catch (SQLException e) {
            // Internal Server Error
            exchange.setStatusCode(500);
            exchange.getResponseSender().send("Error: Internal Server Error");
        }
    }

    private Map<String, Object> parseRequestBody(HttpServerExchange exchange) {
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
