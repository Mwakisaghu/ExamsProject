package auth;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class AuthFilter {
    public HttpHandler doFilter(HttpHandler next) {
        return exchange -> {
            // Extracting token from request headers & params
            String token = extractToken(exchange);

            // Token validation
            if (TokenManager.validateToken(token)) {
                // If Token is valid - check if it should be refreshed
                if (shouldRefreshToken(token)) {
                    // Refresh the token
                    String refreshedToken = TokenManager.refreshToken(token);

                    // Updating the token in req/res - when needed
                    updateToken(exchange, refreshedToken);
                }

                // Authentication check
                if (authReq(exchange)) {
                    // continue if auth is a success
                    next.handleRequest(exchange);
                }
            } else {
                // Invalid Token - unauthorized
                exchange.setStatusCode(401);
                exchange.getResponseSender().send("Unauthorized!!");
            }
        };
    }

    private boolean authReq(HttpServerExchange exchange) throws InvalidAlgorithmParameterException, XPathExpressionException, NoSuchPaddingException, IllegalBlockSizeException, ParserConfigurationException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException, SAXException {
        // Extracting username and password from request
        String username = exchange.getRequestHeaders().getFirst("username");
        String password = exchange.getRequestHeaders().getFirst("password");

        // Authenticating user
        String authToken = AuthManager.authenticateUser(username, password);

        // Checking if authentication was successful
        return authToken != null && !authToken.isEmpty();
    }

    private String extractToken(HttpServerExchange exchange) {
        // Extracting token from req headers & params
        return exchange.getRequestHeaders().getFirst("Authorization");
    }

    private boolean shouldRefreshToken(String token) {
        // Get token info from token manager class
        TokenManager.TokenInfo tokenInfo = TokenManager.getTokenInfo(token);
        if (tokenInfo != null) {
            // Retrieving the already defined validity in token manager
            long tokenValidityDuration = TokenManager.getTokenValidityDuration();

            // calculating the remaining validity time of the token
            long remainingValidTime = tokenInfo.getExpirationTime() - System.currentTimeMillis();

            // Checking if remaining valid time is less than already defined valid time
            return remainingValidTime < tokenValidityDuration;
        }
        // Expired Token / Not Found
        return false;
    }

    private void updateToken(HttpServerExchange exchange, String token) {
        // Token update - req headers
        exchange.getRequestHeaders().put(HttpString.tryFromString("Authorization"), token);
    }
}
