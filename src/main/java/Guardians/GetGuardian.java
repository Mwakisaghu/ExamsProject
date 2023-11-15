package Guardians;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static queries.QueryManager.connection;

public class GetGuardian implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the "guardianId" path variable using RestUtils
        String strGuardianId = RestUtils.getPathVar(exchange, "guardianId");

        try {
            // Converting the "guardianId" to an integer
            assert strGuardianId != null;
            int guardianId = Integer.parseInt(strGuardianId);

            // SQL query to retrieve guardian data by guardianId
            String selectQuery = "SELECT * FROM guardian WHERE guardian_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> paramMap = new LinkedHashMap<>();
            paramMap.put(1, guardianId);

            // Executing the SQL query using the QueryManager
            List<LinkedHashMap<String, Object>> guardianMap = QueryManager.executeSelectQuery(selectQuery, (HashMap<Integer, Object>) paramMap);

            if (!guardianMap.isEmpty()) {
                // The query result is a list of rows, and each row is a LinkedHashMap
                LinkedHashMap<String, Object> guardianData = guardianMap.get(0);

                // Creating a JSON response
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
                String strJsonResponse = gson.toJson(guardianData);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Guardian not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Guardian not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid guardianId is provided - bad req
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid guardianId");
        } catch (Exception e) {
            // Handle other errors
            e.printStackTrace();
            // Internal server error
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Internal Server Error");
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

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return context.serialize(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}
