package class_tier;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static queries.QueryManager.connection;

public class UpdateTier implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extract the "tier_id" path variable using RestUtils
        String strTierId = RestUtils.getPathVar(exchange, "tierId");
        String reqBody = RestUtils.getRequestBody(exchange);

        // Checking if strTierId is a valid integer
        if (!isInteger(strTierId)) {
            // Handle the case where an invalid tier_id is provided
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid class_tier_id. The provided teacher_id must be a valid integer.");
            return;
        }

        try {
            // Parsing the "tier_id" as an integer
            assert strTierId != null;
            int tierId = Integer.parseInt(strTierId);

            // Parsing the request body as JSON
            Gson gson = new Gson();
            @SuppressWarnings("unchecked")
            HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

            // Checking for the request body
            if (requestBodyMap == null) {
                StatusResponses.sendErrorResponse(exchange, "Error: Invalid request - RequestBody Map not found");
            } else {
                // Build the SQL update statement based on the fields in the request body
                StringBuilder updateSqlBuilder = new StringBuilder("UPDATE class_tier SET");
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
                updateSqlBuilder.append(" WHERE class_tier_id = ?");
                updateMap.put(paramIndex, tierId);

                // Execute the SQL update using the QueryManager
                int affectedRows = QueryManager.executeUpdateQuery(updateSqlBuilder.toString(), updateMap).size();

                // Checking the number of affected rows to determine success
                if (affectedRows > 0) {
                    // Success response
                    StatusResponses.sendSuccessResponse(exchange, "Success: Updated " + affectedRows + " rows");
                } else {
                    // The class_tier with the provided ID not found
                    StatusResponses.sendErrorResponse(exchange, "Error: Class_tier not found");
                }
            }
        } catch (NumberFormatException e) {
            // Handles the case where an invalid tier_id is provided
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid class_tier_id. Please provide a valid integer.");
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
