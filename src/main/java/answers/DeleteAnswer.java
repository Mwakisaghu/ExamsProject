package answers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeleteAnswer implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strAnswerId = RestUtils.getPathVar(exchange, "answerId");

        Connection connection = null;
        try {
            assert strAnswerId != null;
            int answerId = Integer.parseInt(strAnswerId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM guardian WHERE answer_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, answerId);

            // Executing the SQL delete using the QueryManager
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            if (rowsDeleted > 0) {
                // Success response
                StatusResponses.sendSuccessResponse(exchange, "Success: Deleted " + rowsDeleted + " rows");
            } else {
                // User not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Answer not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid guardianId is provided - bad request
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid answerId");
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
