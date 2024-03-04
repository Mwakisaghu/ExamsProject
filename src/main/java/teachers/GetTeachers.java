package teachers;

import com.google.gson.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import jdk.jshell.Snippet;
import queries.QueryManager;
import responses.StatusResponses;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static queries.QueryManager.connection;

@SuppressWarnings("unused")
public class GetTeachers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            // SQL query to retrieve teachers data with additional filtering criteria if provided
            String selectQuery = "SELECT * FROM teachers";

            // Creating a parameter map for the query
            HashMap<Integer, Object> paramMap = new LinkedHashMap<>();

            // Executing the SQL query using the QueryManager
            List<LinkedHashMap<String, Object>> result = QueryManager.executeSelectQuery(selectQuery, paramMap);

            if (!result.isEmpty()) {
                // The query result is a list of rows, and each row is a LinkedHashMap
                List<Map<String, Object>> teachersData = new ArrayList<>();

                for (LinkedHashMap<String, Object> row : result) {
                    teachersData.add(row);
                }

                // Creating a JSON response
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
                String strJsonResponse = gson.toJson(teachersData);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // No teachers found
                StatusResponses.send404NotFoundResponse(exchange, "Error: No teachers found");
            }
        } catch (Exception e) {
            // Handle other errors
            e.printStackTrace();
            StatusResponses.sendInternalServerErrorResponse(exchange, "Error: Internal Server Error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
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
