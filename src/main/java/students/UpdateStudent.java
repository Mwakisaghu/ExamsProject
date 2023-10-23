package students;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.Connection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class UpdateStudent implements HttpHandler {
    private final Connection connection;

    public UpdateStudent(Connection connection) {

        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the student ID and updating the date of birth from the request
        Map<String, Deque<String>> parameters = exchange.getQueryParameters();
        String studentIdParam = parameters.get("studentId").getFirst();
        String updatedDateOfBirth = parameters.get("date_of_birth").getFirst();

        if (studentIdParam == null || updatedDateOfBirth == null) {
            // Bad Request - if id param is missing
            exchange.setStatusCode(400);
            exchange.getResponseSender().send(" Error : Missing studentId or date_of_birth parameters");
            return;
        }

        try {
            // Parsing the student ID to an integer
            int studentId = Integer.parseInt(studentIdParam);

            // Update the info
            String updateSql = "UPDATE students SET date_of_birth = ? WHERE student_id = ?";

            Map<Integer, Object> updateMap = new HashMap<>();
            updateMap.put(1, updatedDateOfBirth);
            updateMap.put(2, studentId);

            int rowCount = QueryManager.executeUpdateQuery(updateSql, updateMap);

            // Updating the student's information
            if (rowCount > 0) {
                // success response - 200
                exchange.setStatusCode(200);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Success: Student info updated successfully");
            } else {
                // if student with the provided ID not found
                exchange.setStatusCode(404);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Student not found");
            }
        } catch (NumberFormatException e) {
            // invalid student ID format & send a bad request
            exchange.setStatusCode(400);
            exchange.getResponseSender().send("Error: Invalid studentId parameter");
        } catch (Exception e) {
            // Internal Server Error
            exchange.setStatusCode(500);
            exchange.getResponseSender().send("Error : Internal Server Error");
        }
    }
}
