package students;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetStudents implements HttpHandler {
    private final Connection connection;

    public GetStudents(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        ResultSet resultSet = retrieveStudentsFromDatabase();

        if (resultSet != null) {
            // Converting the ResultSet into a JSON
            String jsonResponse = convertResultSetToJson(resultSet);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Internal server errors
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Failed to retrieve student data from the database");
        }
    }

    private ResultSet retrieveStudentsFromDatabase() {
        String selectQuery = "SELECT * FROM students";

        try {
            // QueryManager to execute the query
            return QueryManager.executeSelectQuery(selectQuery, new HashMap<>());
        } catch (SQLException e) {
            // Handle database query errors here
            e.printStackTrace();
            return null;
        }
    }

    private String convertResultSetToJson(ResultSet resultSet) throws SQLException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> students = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, Object> studentData = new HashMap<>();
            studentData.put("student_id", resultSet.getInt("student_id"));
            studentData.put("first_name", resultSet.getString("first_name"));
            studentData.put("last_name", resultSet.getString("last_name"));
            studentData.put("gender", resultSet.getString("gender"));
            students.add(studentData);
        }
        return objectMapper.writeValueAsString(students);
    }
}