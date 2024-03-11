package users;

import auth.UserAccountsManager;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import responses.StatusResponses;
import rest.RestUtils;

import java.util.Map;

public class GetUserDetailsHandler implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Extracting the username from the request path
        String username = RestUtils.getPathVar(exchange, "username");

        // Check if username is provided in the path
        if (username == null || username.isEmpty()) {
            StatusResponses.sendErrorResponse(exchange, "Error: Username not provided in the request path!");
            return;
        }

        // Retrieve user credentials
        Map<String, String> credentials = UserAccountsManager.getUserCredentials(username);

        // Check if user exists
        if (credentials == null) {
            StatusResponses.sendErrorResponse(exchange, "Error: User not found!");
            return;
        }

        // Send user details in the response
        String userDetails = "Username: " + credentials.get("username") + "Password: " + credentials.get("password");
        StatusResponses.sendSuccessResponse(exchange, userDetails);
    }
}
