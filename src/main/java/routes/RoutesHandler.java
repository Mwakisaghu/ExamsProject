package routes;

import io.undertow.server.HttpHandler;
import io.undertow.server.RoutingHandler;
import students.*;

public class RoutesHandler {
    public static HttpHandler createRoutingHandler() {
        return new RoutingHandler()
                .get("/students", new GetStudents())
                .post("/students/create", new CreateStudent())
                .get("/students/{studentId}", new GetStudent())
                .put("/students/update", new UpdateStudent())
                .delete("/students/delete", new DeleteStudent());
    }
}
