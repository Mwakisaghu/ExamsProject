package students;

import com.google.gson.Gson;


import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import org.xml.sax.SAXException;
import queries.QueryManager;
import responses.StatusResponses;
import rest.RestUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static queries.QueryManager.connection;

public class CreateStudent implements HttpHandler {
    public void handleRequest(HttpServerExchange exchange) throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        // Extract the request body
        String reqBody = RestUtils.getRequestBody(exchange);

        Gson gson = new Gson();
        HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

        // Check for the presence of a valid request body
        if (requestBodyMap == null || !isValidRequest(requestBodyMap)) {
            // Bad Request
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid request - missing or invalid fields");
            return;
        }

        // Build dynamic SQL INSERT query
        String tableName = "students";
        String columns = String.join(", ", requestBodyMap.keySet());
        String values = String.join(", ", new String(new char[requestBodyMap.size()]).replace("\0", "?").split(""));

        String insertQuery = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";

        // Prepare the parameters for the INSERT operation
        Map<Integer, Object> insertMap = new HashMap<>();
        int parameterIndex = 1;
        for (Object value : requestBodyMap.values()) {
            insertMap.put(parameterIndex++, value);
        }

        try {
            // Execute the dynamic SQL INSERT query using the QueryManager
            QueryManager.executeInsertQuery(insertQuery, insertMap);

            // Created Successfully
            StatusResponses.sendSuccessResponse(exchange, "Student Created successfully");
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

    private boolean isValidRequest(Map<String, Object> requestBody) {
        // Check if required fields are present and have valid values
        return requestBody.containsKey("first_name") && requestBody.containsKey("middle_name") &&
                requestBody.containsKey("last_name") && requestBody.containsKey("class_tier_id") &&
                requestBody.containsKey("date_of_birth") && requestBody.containsKey("gender") &&
                requestBody.containsKey("email_address");
    }
}
