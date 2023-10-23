package students;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;
import java.sql.SQLException;

public class DeleteStudent implements HttpHandler {
    private final Connection connection;

    public DeleteStudent(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the student ID from the request
        String studentIdParam = exchange.getQueryParameters().get("studentId").getFirst();

        if (studentIdParam == null) {
            // Checking if the studentId is missing & send Bad Request
            exchange.setStatusCode(400);
            exchange.getResponseSender().send("Error: The studentId is missing");
            return;
        }

        // executing the DELETE query - query runner
        String deleteQuery = "DELETE FROM students WHERE student_id = ?";
        Map<Integer, Object> deleteMap = new HashMap<>();

        try {
            // Parsing the studentIdParam to an integer
            int studentId = Integer.parseInt(studentIdParam);
            deleteMap.put(1, studentId);

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
}
