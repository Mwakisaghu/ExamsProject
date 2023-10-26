package path;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import routes.RoutesHandler;

import java.sql.Connection;

public class PathHandler implements HttpHandler {
    private final HttpHandler next;

    public PathHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        // Execute the next handler - RoutingHandler
        next.handleRequest(exchange);
    }

    public static HttpHandler createPathHandler(Connection connection) {
        // Creating the RoutingHandler - add the routes
        HttpHandler routingHandler = RoutesHandler.createRoutingHandler();

        // Wrap the routing handler in a custom PathHandler for pre,post-processing
        return new PathHandler(routingHandler);
    }
}
