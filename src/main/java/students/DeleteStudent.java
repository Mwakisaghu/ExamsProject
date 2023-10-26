package students;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;
import rest.RestUtils;

import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

public class DeleteStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the studentId from the request body
        String studentId = RestUtils.getPathVar(exchange, "student_id");

        // Sql query
        String deleteQuery = "DELETE FROM students WHERE student_id = ?";

        // Preparing the parameters for the delete operation
        Map<String, Object> deleteMap = new HashMap<>();
        deleteMap.put("student_id", studentId);

        try {
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            if (rowsDeleted > 0) {
                // success response status -  code 204 (No Content)
                exchange.setStatusCode(204);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(String.valueOf(rowsDeleted));
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
}
