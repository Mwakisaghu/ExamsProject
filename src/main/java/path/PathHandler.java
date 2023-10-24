package path;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import routes.RoutesHandler;

import static routes.RoutesHandler.createRoutesHandler;

public class PathHandler {
    public static HttpHandler create() {
        io.undertow.server.handlers.PathHandler pathHandler = Handlers.path();

        // Mapping the root path to the RoutesHandler
        pathHandler.addPrefixPath("/api", createRoutesHandler());

        return pathHandler;
    }
}
