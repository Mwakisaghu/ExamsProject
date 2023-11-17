package multiple_choices;

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

public class GetChoice implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the "choiceId" path variable using RestUtils
        String strChoiceId = RestUtils.getPathVar(exchange, "choiceId");

        try {
            // Converting the "choiceId" to an integer
            assert strChoiceId != null;
            int choiceId = Integer.parseInt(strChoiceId);

            // SQL query to retrieve choices data by choiceId
            String selectQuery = "SELECT * FROM multiple_choices WHERE multiple_choice_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> paramMap = new LinkedHashMap<>();
            paramMap.put(1, choiceId);

            // Executing the SQL query using the QueryManager
            List<LinkedHashMap<String, Object>> choicesMap = QueryManager.executeSelectQuery(selectQuery, (HashMap<Integer, Object>) paramMap);

            if (!choicesMap.isEmpty()) {
                // The query result is a list of rows, and each row is a LinkedHashMap
                LinkedHashMap<String, Object> choicesData = choicesMap.get(0);

                // Creating a JSON response
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
                String strJsonResponse = gson.toJson(choicesData);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Choice not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Choices not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid choiceId is provided - bad req
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid choiceId");
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
