package users;

import auth.UserAccountsManager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import responses.StatusResponses;
import rest.RestUtils;

import java.io.IOException;
import java.util.HashMap;

public class RegisterUserHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the req body
        String reqBody = RestUtils.getRequestBody(exchange);

        // Check for valid req body
        if (reqBody == null || reqBody.isEmpty()) {
            // Bad req
            StatusResponses.sendErrorResponse(exchange, "Error: Invalid request - missing req body!");
            return;
        }

        // Parsing the req body into a Map
        HashMap<String, String> requestBodyMap = RestUtils.getQueryParams(exchange, "username", "password");

        // Retrieving username and password from req
        String username = requestBodyMap.get("username");
        String password = requestBodyMap.get("password");

        // Register a user
        boolean registered = UserAccountsManager.register(username, password);

        if (registered) {
            StatusResponses.sendSuccessResponse(exchange, "User registered successfully");
        } else {
            StatusResponses.sendErrorResponse(exchange, "Error: Failed to register the user!");
        }
    }
}
