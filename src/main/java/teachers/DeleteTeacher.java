package teachers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import queries.QueryManager;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static queries.QueryManager.connection;

public class DeleteTeacher implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strTeacherId = RestUtils.getPathVar(exchange, "teacherId");

        try {
            assert strTeacherId != null;
            int teacherId = Integer.parseInt(strTeacherId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM teachers WHERE teacher_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, teacherId);

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
                exchange.getResponseSender().send("Error: Teacher not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid teacherId is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Error: Invalid teacherId");
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
}
