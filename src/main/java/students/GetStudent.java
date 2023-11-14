package students;

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

public class GetStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the "studentId" path variable using RestUtils
        String strStudentId = RestUtils.getPathVar(exchange, "studentId");

        try {
            // Converting the "studentId" to an integer
            assert strStudentId != null;
            int teacherId = Integer.parseInt(strStudentId);

            // SQL query to retrieve teachers data by teacherId
            String selectQuery = "SELECT * FROM students WHERE student_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> paramMap = new LinkedHashMap<>();
            paramMap.put(1, strStudentId);

            // Executing the SQL query using the QueryManager
            List<LinkedHashMap<String, Object>> studentMap = QueryManager.executeSelectQuery(selectQuery, (HashMap<Integer, Object>) paramMap);

            if (!studentMap.isEmpty()) {
                // The query result is a list of rows, and each row is a LinkedHashMap
                LinkedHashMap<String, Object> studentData = studentMap.get(0);

                // Creating a JSON response
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
                String strJsonResponse = gson.toJson(studentData);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Student not found
                StatusResponses.send404NotFoundResponse(exchange, "Error: Student not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid studentId is provided - bad req
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid studentId");
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
