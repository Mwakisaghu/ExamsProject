package students;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import queries.QueryManager;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeleteStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strStudentId = RestUtils.getPathVar(exchange, "studentId");

        try {
            assert strStudentId != null;
            int studentId = Integer.parseInt(strStudentId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM students WHERE student_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, studentId);

            try {
                // Executing the SQL delete using the QueryManager
                int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

                if (rowsDeleted > 0) {
                    // Success response
                    exchange.setStatusCode(StatusCodes.OK);
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.getResponseSender().send("Success: Deleted " + rowsDeleted + " rows");
                } else {
                    // User not found
                    exchange.setStatusCode(StatusCodes.NOT_FOUND);
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.getResponseSender().send("Error: Student not found");
                }
            } catch (SQLException e) {
                // Handling Database Errors
                e.printStackTrace();

                exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Error: Internal Server Error");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid studentId is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Error: Invalid studentId");
        }
    }
}
