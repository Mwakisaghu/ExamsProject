package responses;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public class StatusResponses {

    // Utility method - send a success response
    public static void sendSuccessResponse(HttpServerExchange exchange, String message) {
        exchange.setStatusCode(StatusCodes.OK);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(message);
    }

    // Utility method - send an error response
    public static void sendErrorResponse(HttpServerExchange exchange, String errorMessage) {
        exchange.setStatusCode(StatusCodes.BAD_REQUEST);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(errorMessage);
    }

    public static  void send404NotFoundResponse (HttpServerExchange exchange, String errorMessage) {
        exchange.setStatusCode(StatusCodes.NOT_FOUND);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(errorMessage);
    }

    public static void sendInternalServerErrorResponse(HttpServerExchange exchange, String errorMessage) {
        exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(errorMessage);
    }
}
