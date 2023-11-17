package multiple_choices;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeleteChoice implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strChoiceId = RestUtils.getPathVar(exchange, "choiceId");

        Connection connection = null;
        try {
            assert strChoiceId != null;
            int choiceId = Integer.parseInt(strChoiceId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM multiple_choices WHERE multiple_choice_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, choiceId);

            // Executing the SQL delete using the QueryManager
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            if (rowsDeleted > 0) {
                // Success response
                StatusResponses.sendSuccessResponse(exchange, "Success: Deleted " + rowsDeleted + " rows");
            } else {
                // User not found
                StatusResponses.send404NotFoundResponse(exchange, "Error : Choice not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid choiceId is provided - bad request
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid choiceId");
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
