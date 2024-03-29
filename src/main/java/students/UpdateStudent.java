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

import static queries.QueryManager.connection;

public class UpdateStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extract the "student_id" path variable using RestUtils
        String strStudentId = RestUtils.getPathVar(exchange, "studentId");
        String reqBody = RestUtils.getRequestBody(exchange);

        // Checking if strStudentId is a valid integer
        if (!isInteger(strStudentId)) {
            // Handle the case where an invalid student_id is provided
            sendErrorResponse(exchange, "Error: Invalid student_id. The provided student_id must be a valid integer.");
            return;
        }

        try {
            // Parsing the "student_id" as an integer
            assert strStudentId != null;
            int studentId = Integer.parseInt(strStudentId);

            // Parsing the request body as JSON
            Gson gson = new Gson();
            @SuppressWarnings("unchecked")
            HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

            // Checking for the request body
            if (requestBodyMap == null) {
                sendErrorResponse(exchange, "Error: Invalid request - RequestBody Map not found");
            } else {
                // Build the SQL update statement based on the fields in the request body
                StringBuilder updateSqlBuilder = new StringBuilder("UPDATE teachers SET");
                Map<Integer, Object> updateMap = new HashMap<>();
                int paramIndex = 1;

                for (String key : requestBodyMap.keySet()) {
                    if (paramIndex > 1) {
                        updateSqlBuilder.append(",");
                    }
                    updateSqlBuilder.append(" ").append(key).append(" = ?");
                    updateMap.put(paramIndex, requestBodyMap.get(key));
                    paramIndex++;
                }
                updateSqlBuilder.append(" WHERE student_id = ?");
                updateMap.put(paramIndex, studentId);

                // Execute the SQL update using the QueryManager
                int affectedRows = QueryManager.executeUpdateQuery(updateSqlBuilder.toString(), updateMap).size();

                // Checking the number of affected rows to determine success
                if (affectedRows > 0) {
                    // Success response
                    sendSuccessResponse(exchange, "Success: Updated " + affectedRows + " rows");
                } else {
                    // The student with the provided ID not found
                    sendErrorResponse(exchange, "Error: Student not found");
                }
            }
        } catch (NumberFormatException e) {
            // Handles the case where an invalid student_id is provided
            sendErrorResponse(exchange, "Error: Invalid student_id. Please provide a valid integer.");
        }
        finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Utility method - send a success response
    private void sendSuccessResponse(HttpServerExchange exchange, String message) {
        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(message);
    }

    // Utility method - send an error response
    private void sendErrorResponse(HttpServerExchange exchange, String errorMessage) {
        exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(errorMessage);
    }

    // Utility method - checking if a string is a valid integer
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
