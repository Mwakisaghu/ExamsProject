package answers;

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

public class GetAnswer implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the "answerId" path variable using RestUtils
        String strAnswerId = RestUtils.getPathVar(exchange, "answerId");

        try {
            // Converting the "answerId" to an integer
            assert strAnswerId != null;
            int answerId = Integer.parseInt(strAnswerId);

            // SQL query to retrieve guardian data by answerId
            String selectQuery = "SELECT * FROM answers WHERE answer_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> paramMap = new LinkedHashMap<>();
            paramMap.put(1, answerId);

            // Executing the SQL query using the QueryManager
            List<LinkedHashMap<String, Object>>answerMap = QueryManager.executeSelectQuery(selectQuery, (HashMap<Integer, Object>) paramMap);

            if (!answerMap.isEmpty()) {
                // The query result is a list of rows, and each row is a LinkedHashMap
                LinkedHashMap<String, Object> answerData = answerMap.get(0);

                // Creating a JSON response
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
                String strJsonResponse = gson.toJson(answerData);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Guardian not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Answer not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid guardianId is provided - bad req
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid answerId");
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
