package routes;

import Guardians.*;
import exams.*;
import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Methods;
import org.xml.sax.SAXException;
import rest.CorsHandler;
import rest.Dispatcher;
import rest.FallBack;
import rest.InvalidMethod;
import students.*;
import subjects.*;
import teachers.*;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class RoutesHandler {
    public static RoutingHandler students () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
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

    public static RoutingHandler teachers () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return  Handlers.routing()
                .get("/", new Dispatcher(new GetTeachers()))
                .get("/{teacherId}", new Dispatcher(new GetTeacher()))
                .post("/", new BlockingHandler(new CreateTeacher()))
                .put("/{teacherId}", new BlockingHandler(new UpdateTeacher()))
                .delete("/{teacherId}", new Dispatcher(new DeleteTeacher()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler subjects () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetSubjects()))
                .get("/{subjectId}", new Dispatcher(new GetSubject()))
                .post("/", new BlockingHandler(new CreateSubject()))
                .put("/{subjectId}", new BlockingHandler(new UpdateSubject()))
                .delete("/{subjectId}", new Dispatcher(new DeleteSubject()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler exams () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetExams()))
                .get("/{examId}", new Dispatcher(new GetExam()))
                .post("/", new BlockingHandler(new CreateExam()))
                .put("/{examId}", new BlockingHandler(new UpdateExam()))
                .delete("/{examId}", new Dispatcher(new DeleteExam()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler guardians () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetGuardians()))
                .get("/{guardianId}", new Dispatcher(new GetGuardian()))
                .post("/", new BlockingHandler(new CreateGuardian()))
                .put("/{guardianId}", new BlockingHandler(new UpdateGuardian()))
                .delete("/{guardianId}", new Dispatcher(new DeleteGuardian()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
}
