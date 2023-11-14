package students;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
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
    @Override
    public void handleRequest(HttpServerExchange exchange) throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        // Extract the request body
        String reqBody = RestUtils.getRequestBody(exchange);

        Gson gson = new Gson();
        HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

        // Check for the presence of a valid request body
        if (requestBodyMap == null) {
            // Bad Request
            StatusResponses.sendErrorResponse(exchange, "Error : Invalid request - request body not found");
        }

        // Define the SQL INSERT query
        String insertQuery = "INSERT INTO students ( first_name, middle_name, last_name, class_tier_id, date_of_birth, gender, email_address, created_at) " +
                "VALUES ( ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        // Prepare the parameters for the INSERT operation
        Map<Integer, Object> insertMap = new HashMap<>();
        assert requestBodyMap != null;
        insertMap.put(1, requestBodyMap.get("first_name"));
        insertMap.put(2, requestBodyMap.get("middle_name"));
        insertMap.put(3, requestBodyMap.get("last_name"));
        insertMap.put(4, requestBodyMap.get("class_tier_id"));
        insertMap.put(5, requestBodyMap.get("date_of_birth"));
        insertMap.put(6, requestBodyMap.get("gender"));
        insertMap.put(7, requestBodyMap.get("email_address"));

        try {
            // Execute the SQL INSERT query using the QueryManager
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
}
