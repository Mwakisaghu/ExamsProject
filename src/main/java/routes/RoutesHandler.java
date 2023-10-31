package routes;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import rest.Dispatcher;
import students.*;

import java.sql.Connection;

public class RoutesHandler {
    public static RoutingHandler configureRoutes(Connection connection) {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetStudents()))
                .get("/{studentId}", new Dispatcher(new GetStudent()))
                .post("/", new BlockingHandler(new CreateStudent()))
                .put("/{studentId}", new BlockingHandler(new UpdateStudent()))
                .delete("/{studentId}", new Dispatcher(new DeleteStudent()));
    }
}
