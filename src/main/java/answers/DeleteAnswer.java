package answers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Deque;
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
            StringBuilder deleteQuery = new StringBuilder("DELETE FROM guardian WHERE answer_id = ?");
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, answerId);

            // Apply filtering if necessary
            Deque<String> filterParams = exchange.getQueryParameters().get("filter");
            String filterCriteria = filterParams != null ? filterParams.getFirst() : "";
            if (!filterCriteria.isEmpty()) {
                deleteQuery.append(" AND ").append(filterCriteria);
            }

            // Apply sorting if necessary
            Deque<String> sortByParams = exchange.getQueryParameters().get("sort");
            String sortBy = sortByParams != null ? sortByParams.getFirst() : "";
            if (!sortBy.isEmpty()) {
                deleteQuery.append(" ORDER BY ").append(sortBy);
            }

            // Executing the SQL delete using the QueryManager
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery.toString(), deleteMap);

            if (rowsDeleted > 0) {
                // Success response
                StatusResponses.sendSuccessResponse(exchange, "Success: Deleted " + rowsDeleted + " rows");
            } else {
                // Answer not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Answer not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid answerId is provided - bad request
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
