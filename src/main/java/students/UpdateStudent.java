package students;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UpdateStudent implements HttpHandler {
    private final Connection connection;

    public UpdateStudent(Connection connection) {

        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the student info
        Map<String, Object> requestBodyMap = parseReqBody(exchange);

        //Check for the request body
        if (requestBodyMap == null) {
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error : Invalid request - RequestBody Map not found");
        }

        // Extracting the studentId from the request body
        assert requestBodyMap != null;
        String studentId = (String) requestBodyMap.get("student_id");

        // Preparing the Sql statement
        String updateSql = "UPDATE students SET date_of_birth = ? WHERE student_id = ?";

        Map<Integer, Object> updateMap = new HashMap<>();
        updateMap.put(1, requestBodyMap.get("date_of_birth"));
        updateMap.put(2, studentId);

        try {
            QueryManager.executeUpdateQuery(updateSql, updateMap);

                // success response - 200
                exchange.setStatusCode(200);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Success: Student info updated successfully");
            } catch (SQLException e) {
                e.printStackTrace();

                // if student with the provided ID not found
                exchange.setStatusCode(404);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Student not found");
            }
    }
    private Map<String, Object> parseReqBody(HttpServerExchange exchange) {
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
