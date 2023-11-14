package subjects;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.xml.sax.SAXException;
import queries.QueryManager;
import rest.RestUtils;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import static queries.QueryManager.connection;

public class CreateSubject implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        // Extract the request body
        String reqBody = RestUtils.getRequestBody(exchange);

        Gson gson = new Gson();
        HashMap<String, Object> requestBodyMap = gson.fromJson(reqBody, HashMap.class);

        // Check for the presence of a valid request body
        if (requestBodyMap == null) {
            // Bad Request
            sendResponse(exchange, 400, "Error : Invalid request - request body not found");
        }

        // Define the SQL INSERT query
        String insertQuery = "INSERT INTO subjects ( subject_name, created_at) " +
                "VALUES ( ?, current_timestamp)";

        // Prepare the parameters for the INSERT operation
        Map<Integer, Object> insertMap = new HashMap<>();
        assert requestBodyMap != null;
        insertMap.put(1, requestBodyMap.get("subject_name"));

        try {
            // Execute the SQL INSERT query using the QueryManager
            QueryManager.executeInsertQuery(insertQuery, insertMap);

            // Created Successfully
            sendResponse(exchange, 201, "Subject Created successfully");
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

    private void sendResponse(HttpServerExchange exchange, int statusCode, String message) {
        exchange.setStatusCode(statusCode);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
        exchange.getResponseSender().send(message);
    }
}
