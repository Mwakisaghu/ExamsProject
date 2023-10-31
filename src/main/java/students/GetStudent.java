package students;

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

public class GetStudent implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the "studentId" path variable using RestUtils
        String strStudentId = RestUtils.getPathVar(exchange, "studentId");

        try {
            // Converting the "studentId" to an integer
            assert strStudentId != null;
            int studentId = Integer.parseInt(strStudentId);

            // SQL query to retrieve student data by studentId
            String selectQuery = "SELECT * FROM students WHERE student_id = ?";

            // Creating a parameter map for the query
            Map<Integer, Object> paramMap = new HashMap<>();
            paramMap.put(1, studentId);

            // Executing the SQL query using the QueryManager
            ResultSet resultSet = QueryManager.executeSelectQuery(selectQuery, paramMap);

            if (resultSet.next()) {
                // Extracting student data from the result set
                int studentIdResult = resultSet.getInt("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String dateOfBirth = resultSet.getString("date_of_birth");
                String gender = resultSet.getString("gender");

                // Creating a JSON response
                Gson gson = new Gson();
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("student_id", studentIdResult);
                jsonResponse.put("first_name", firstName);
                jsonResponse.put("last_name", lastName);
                jsonResponse.put("date_of_birth", dateOfBirth);
                jsonResponse.put("gender", gender);

                String strJsonResponse = gson.toJson(jsonResponse);

                // Setting the HTTP response status code to 200 (OK)
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content type to "application/json"
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the JSON response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Student not found
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Student not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid studentId is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid studentId");
        } catch (SQLException e) {
            // Handle database errors
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Internal Server Error");
        }
    }
}
