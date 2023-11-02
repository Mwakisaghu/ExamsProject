package routes;

import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Methods;
import rest.CorsHandler;
import rest.Dispatcher;
import rest.FallBack;
import rest.InvalidMethod;
import students.*;
import teachers.CreateTeacher;
import teachers.GetTeacher;
import teachers.GetTeachers;

import java.sql.Connection;

public class RoutesHandler {
    public static RoutingHandler students (Connection connection) {
        return Handlers.routing()
                // Students
                .get("/", new Dispatcher(new GetStudents()))
                .get("/{studentId}", new Dispatcher(new GetStudent()))
                .post("/", new BlockingHandler(new CreateStudent()))
                .put("/{studentId}", new BlockingHandler(new UpdateStudent()))
                .delete("/{studentId}", new Dispatcher(new DeleteStudent()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler teachers (Connection connection) {
        return  Handlers.routing()
                .get("/", new Dispatcher(new GetTeachers()))
                .get("/{teacherId}", new Dispatcher(new GetTeacher()))
                .post("/", new BlockingHandler(new CreateTeacher()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
}
