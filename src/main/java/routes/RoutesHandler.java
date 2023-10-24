package routes;

import io.undertow.server.RoutingHandler;
import students.CreateStudent;
import students.DeleteStudent;
import students.GetStudent;
import students.UpdateStudent;

import static queries.QueryManager.connection;

public class RoutesHandler {
    public static RoutingHandler create() {
        RoutingHandler routingHandler = new RoutingHandler();

        // Define routes and attach corresponding handlers
        routingHandler.add("GET", "/students/get", new GetStudent(connection));
        routingHandler.add("POST", "/students/create", new CreateStudent(connection));
        routingHandler.add("PUT", "/students/update", new UpdateStudent(connection));
        routingHandler.add("DELETE", "/students/delete", new DeleteStudent(connection));

        return routingHandler;
    }
}
