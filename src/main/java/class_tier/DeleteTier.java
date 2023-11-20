package class_tier;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeleteTier implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strTierId = RestUtils.getPathVar(exchange, "tierId");

        Connection connection = null;
        try {
            assert strTierId != null;
            int tierId = Integer.parseInt(strTierId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM class_tier WHERE tier_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, tierId);

            // Executing the SQL delete using the QueryManager
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            if (rowsDeleted > 0) {
                // Success response
                StatusResponses.sendSuccessResponse(exchange, "Success: Deleted " + rowsDeleted + " rows");
            } else {
                // User not found
                StatusResponses.send404NotFoundResponse(exchange, "Error:Class_tier not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid tierId is provided - bad request
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid tierId");
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
