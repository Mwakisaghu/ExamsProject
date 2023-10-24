package routes;

import io.undertow.server.RoutingHandler;
import students.*;

import static queries.QueryManager.connection;

public class RoutesHandler {
    public static RoutingHandler createRoutesHandler() {
        RoutingHandler routingHandler = new RoutingHandler();

        // Define routes and attach corresponding handlers
        routingHandler.add("GET", "/students/get", new GetStudents(connection));
        routingHandler.add("GET", "students/get/{studentId}", new GetStudent(connection));
        routingHandler.add("POST", "/students/create", new CreateStudent(connection));
        routingHandler.add("PUT", "/students/update/{studentId}", new UpdateStudent(connection));
        routingHandler.add("DELETE", "/students/delete/{studentId}", new DeleteStudent(connection));

        return routingHandler;
    }
}
