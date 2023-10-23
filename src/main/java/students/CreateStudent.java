package students;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import queries.QueryManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class CreateStudent implements HttpHandler {
    private final Connection connection;

    public CreateStudent(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extract student information from the request
        Map<String, Deque<String>> parameters = exchange.getQueryParameters();
        String studentId = parameters.get("student_id").getFirst();
        String firstName = parameters.get("first_name").getFirst();
        String middleName = parameters.get("middle_name").getFirst();
        String lastName = parameters.get("last_name").getFirst();
        String classTierId = parameters.get("class_tier_id").getFirst();
        String dateOfBirth = parameters.get("date_of_birth").getFirst();
        String gender = parameters.get("gender").getFirst();
        String emailAddress = parameters.get("email_address").getFirst();

        String insertSql = "INSERT INTO students (student_id, first_name, middle_name, last_name, class_tier_id, date_of_birth, gender, email_address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        // Prepare the parameters for the insert operation
        Map<Integer, Object> insertMap = new HashMap<>();
        insertMap.put(1, studentId);
        insertMap.put(2, firstName);
        insertMap.put(3, middleName);
        insertMap.put(4, lastName);
        insertMap.put(5, classTierId);
        insertMap.put(6, dateOfBirth);
        insertMap.put(7, gender);
        insertMap.put(8, emailAddress);

        try {
            // Executing the insert query & getting the generated keys and row count
            int rowCount = QueryManager.executeInsertQuery(insertSql, insertMap);

            if (rowCount > 0) {
                // Status - Created
                exchange.setStatusCode(201);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Student created successfully");
            } else {
                // Status - Internal Server Error
                exchange.setStatusCode(500);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                exchange.getResponseSender().send("Student cannot be created");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Status - Internal Server Error
            exchange.setStatusCode(500);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            exchange.getResponseSender().send("Internal Server Error");
        }
    }
}
