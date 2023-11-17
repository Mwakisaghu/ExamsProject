package grades;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DeleteGrade implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strGradeId = RestUtils.getPathVar(exchange, "gradeId");

        Connection connection = null;
        try {
            assert strGradeId != null;
            int gradeId = Integer.parseInt(strGradeId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM grades WHERE grade_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, gradeId);

            // Executing the SQL delete using the QueryManager
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            if (rowsDeleted > 0) {
                // Success response
                StatusResponses.sendSuccessResponse(exchange, "Success: Deleted " + rowsDeleted + " rows");
            } else {
                // User not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Grade not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid gradeId is provided - bad request
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid gradeId");
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
