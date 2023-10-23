package students;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import com.fasterxml.jackson.databind.ObjectMapper;
import queries.QueryManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class GetStudent implements HttpHandler {
    private Connection connection;

    public GetStudent(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the StudentId
        String studentIdParam = exchange.getQueryParameters().get("studentId").getFirst();

        if (studentIdParam == null) {
            exchange.setStatusCode(400);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(" Error : StudentId is not provided");
            return;
        }

        // Retrieving the student's data from the database
        String selectQuery = "SELECT * FROM students WHERE student_id = ?";

        Map<Integer, Object> selectMap = new HashMap<>();
        selectMap.put(1, Integer.parseInt(studentIdParam)); // Convert to int

        ResultSet resultSet = null;
        try {
            resultSet = QueryManager.executeSelectQuery(selectQuery, selectMap);

            if (resultSet.next()) {
                String studentIdResult = resultSet.getString("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");

                // Creating a JSON object with student data
                Map<String, Object> jsonResponse = new HashMap<>();
                jsonResponse.put("student_id", studentIdResult);
                jsonResponse.put("first_name", firstName);
                jsonResponse.put("last_name", lastName);
                jsonResponse.put("gender", gender);

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonRes = objectMapper.writeValueAsString(jsonResponse);

                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send(jsonRes);
            } else {
                exchange.setStatusCode(404);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Student not found");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(" Error: Internal Server Error");
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
