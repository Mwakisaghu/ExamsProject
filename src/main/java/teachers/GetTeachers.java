package teachers;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class GetTeachers implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        ResultSet resultSet = retrieveTeachersFromDatabase();

        if (resultSet != null) {
            // Converting the ResultSet into a JSON
            String jsonResponse = convertResultSetToJson(resultSet);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonResponse);
        } else {
            // Internal server errors
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Failed to retrieve teacher data from the database");
        }
    }

    private ResultSet retrieveTeachersFromDatabase() {
        String selectQuery = "SELECT * FROM teachers";

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
        List<Map<String, Object>> teachers = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, Object> teacherData = new HashMap<>();
            teacherData.put("teacher_id", resultSet.getInt("teacher_id"));
            teacherData.put("tsc_number", resultSet.getInt("tsc_number"));
            teacherData.put("first_name", resultSet.getString("first_name"));
            teacherData.put("last_name", resultSet.getString("last_name"));
            teacherData.put("gender", resultSet.getString("gender"));
            teacherData.put("title", resultSet.getString("title"));
            teacherData.put("home_address", resultSet.getString("home_address"));
            teacherData.put("city", resultSet.getString("city"));
            teacherData.put("state", resultSet.getString("state"));
            teacherData.put("mobile_number", resultSet.getString("mobile_number"));
            teacherData.put("email_address", resultSet.getString("email_address"));
            teachers.add(teacherData);
        }
        return objectMapper.writeValueAsString(teachers);
    }
}