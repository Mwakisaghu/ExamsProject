package routes;

import Guardians.*;
import answers.*;
import class_tier.*;
import exams.*;
import grades.*;
import io.undertow.Handlers;
import io.undertow.server.RoutingHandler;
import io.undertow.server.handlers.BlockingHandler;
import io.undertow.util.Methods;
import multiple_choices.*;
import org.xml.sax.SAXException;
import questions.*;
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

    public static RoutingHandler questions () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetQuestion()))
                .get("/{questionId}", new Dispatcher(new GetQuestions()))
                .post("/", new BlockingHandler(new CreateQuestion()))
                .put("/{questionId}", new BlockingHandler(new UpdateQuestion()))
                .delete("/{questionId}", new Dispatcher(new DeleteQuestion()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }


    public static RoutingHandler answers () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetAnswer()))
                .get("/{answerId}", new Dispatcher(new GetAnswers()))
                .post("/", new BlockingHandler(new CreateAnswer()))
                .put("/{answerId}", new BlockingHandler(new UpdateAnswer()))
                .delete("/{answerId}", new Dispatcher(new DeleteAnswer()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler grades () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetGrade()))
                .get("/{gradeId}", new Dispatcher(new GetGrades()))
                .post("/", new BlockingHandler(new CreateGrade()))
                .put("/{gradeId}", new BlockingHandler(new UpdateGrade()))
                .delete("/{gradeId}", new Dispatcher(new DeleteGrade()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler multiple_choices () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetChoice()))
                .get("/{choiceId}", new Dispatcher(new GetChoices()))
                .post("/", new BlockingHandler(new CreateChoice()))
                .put("/{choiceId}", new BlockingHandler(new UpdateChoice()))
                .delete("/{choiceId}", new Dispatcher(new DeleteChoice()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }

    public static RoutingHandler class_tiers () throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        return Handlers.routing()
                .get("/", new Dispatcher(new GetTier()))
                .get("/{tierId}", new Dispatcher(new GetTiers()))
                .post("/", new BlockingHandler(new CreateTier()))
                .put("/{tierId}", new BlockingHandler(new UpdateTier()))
                .delete("/{tierId}", new Dispatcher(new DeleteTier()))
                .add(Methods.OPTIONS, "/*", new CorsHandler())
                .setInvalidMethodHandler(new Dispatcher(new InvalidMethod()))
                .setFallbackHandler(new Dispatcher(new FallBack()));
    }
}
