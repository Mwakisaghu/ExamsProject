package teachers;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import queries.QueryManager;
import rest.RestUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GetTeacher implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the "teacherId" path variable using RestUtils
        String strTeacherId = RestUtils.getPathVar(exchange, "teacherId");

        try {
            // Converting the "teacherId" to an integer
            assert strTeacherId != null;
            int teacherId = Integer.parseInt(strTeacherId);

            // SQL query to retrieve teachers data by teacherId
            String selectQuery = "SELECT * FROM teachers WHERE teacher_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> paramMap = new HashMap<>();
            paramMap.put(1, teacherId);

            // Executing the SQL query using the QueryManager
            ResultSet resultSet = QueryManager.executeSelectQuery(selectQuery, paramMap);

            if (resultSet.next()) {
                // Extracting teacher data from the result set
                int teacherIdResult = resultSet.getInt("teacher_id");
                String tscNumber = resultSet.getString("tsc_number");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");
                String title = resultSet.getString("title");

                // Creating a JSON response
                Gson gson = new Gson();
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("teacher_id", teacherIdResult);
                jsonResponse.put("tsc_number", tscNumber);
                jsonResponse.put("first_name", firstName);
                jsonResponse.put("last_name", lastName);
                jsonResponse.put("gender", gender);
                jsonResponse.put("title", title);

                String strJsonResponse = gson.toJson(jsonResponse);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Teacher not found
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Teacher not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid teacherId is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid teacherId");
        } catch (SQLException e) {
            // Handle database errors
            e.printStackTrace();
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Internal Server Error");
        }
    }
}
