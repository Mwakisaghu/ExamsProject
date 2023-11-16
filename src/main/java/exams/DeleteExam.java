package exams;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static queries.QueryManager.connection;

public class DeleteExam implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        String strExamId = RestUtils.getPathVar(exchange, "examId");

        try {
            assert strExamId != null;
            int examId = Integer.parseInt(strExamId);

            // Defining the SQL delete query
            String deleteQuery = "DELETE FROM exams WHERE exam_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> deleteMap = new HashMap<>();
            deleteMap.put(1, examId);

            // Executing the SQL delete using the QueryManager
            int rowsDeleted = QueryManager.executeDeleteQuery(deleteQuery, deleteMap);

            if (rowsDeleted > 0) {
                // Success response
                StatusResponses.sendSuccessResponse(exchange, "Success: Deleted " + rowsDeleted + " rows");
            } else {
                // User not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Exam not found");
            }
        } catch (NumberFormatException e) {
            // Handles case where an invalid teacherId is provided - bad req
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid examId");
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
