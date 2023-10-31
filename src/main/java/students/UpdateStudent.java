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


public class UpdateStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extract the "student_id" path variable using RestUtils
        String strStudentId = RestUtils.getPathVar(exchange, "studentId");
        String reqBody = RestUtils.getRequestBody(exchange);

        // Checking if strStudentId is a valid integer
        if (!isInteger(strStudentId)) {
            // Handle the case where an invalid student_id is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid student_id. The provided student_id must be a valid integer.");
            return;
        }

        try {
            // Parsing the "student_id" as an integer
            assert strStudentId != null;
            int studentId = Integer.parseInt(strStudentId);

            // Parsing the request body as JSON
            Gson gson = new Gson();
            HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

            // Checking for the request body
            if (requestBodyMap == null) {
                exchange.setStatusCode(StatusCodes.BAD_REQUEST);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Invalid request - RequestBody Map not found");
            } else {
                // Preparing the SQL update statement
                String updateSql = "UPDATE students SET date_of_birth = ? WHERE student_id = ?";

                // Creating a parameter map for the query
                Map<Integer, Object> updateMap = new HashMap<>();

                updateMap.put(1, requestBodyMap.get("date_of_birth"));
                updateMap.put(2, studentId);

                try {
                    // Executing the SQL update using the QueryManager
                    int affectedRows = QueryManager.executeUpdateQuery(updateSql, updateMap);

                    // Checking the number of affected rows to determine success
                    if (affectedRows > 0) {
                        // Success response
                        exchange.setStatusCode(StatusCodes.OK);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send("Success: Updated " + affectedRows + " rows");
                    } else {
                        // Student with the provided ID not found
                        exchange.setStatusCode(StatusCodes.NOT_FOUND);
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send("Error: Student not found");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle database errors
                    exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                    exchange.getResponseSender().send("Error: Internal Server Error");
                }
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid student_id is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid student_id. Please provide a valid integer.");
        }
    }

    // utility method - checking if a string is a valid integer
    private boolean isInteger(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
