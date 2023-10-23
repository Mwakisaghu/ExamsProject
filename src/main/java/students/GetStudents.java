package students;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import com.fasterxml.jackson.databind.ObjectMapper;
import queries.QueryManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GetStudents implements HttpHandler {
    private Connection connection;

    public GetStudents (Connection connection) {

        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Retrieving all students from the db
        List<Map<String, Object>> studentsList = getAllStudents();

        if (!studentsList.isEmpty()) {
            // Creating a JSON array from the list of students
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonRes = objectMapper.writeValueAsString(studentsList);

            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send(jsonRes);
        } else {
            exchange.setStatusCode(404);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: No students found");
        }
    }

    private List<Map<String, Object>> getAllStudents() {
        List<Map<String, Object>> students = new ArrayList<>();

        // SQL query to retrieve all students
        String selectQuery = "SELECT * FROM students";

        ResultSet resultSet = null;
        try {
            resultSet = QueryManager.executeSelectQuery(selectQuery, new HashMap<>());

            while (resultSet.next()) {
                Map<String, Object> student = new HashMap<>();
                student.put("student_id", resultSet.getString("student_id"));
                student.put("first_name", resultSet.getString("first_name"));
                student.put("last_name", resultSet.getString("last_name"));
                student.put("class_tier_id", resultSet.getString("class_tier_id"));
                student.put("date_of_birth", resultSet.getString("date_of_birth"));
                student.put("gender", resultSet.getString("gender"));
                student.put("email_address", resultSet.getString("email_address"));

                students.add(student);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return students;
    }
}
