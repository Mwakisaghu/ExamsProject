package org.example;

import data.ConfigManager;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;
import routes.RoutesHandler;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting REST API");

        try {
            ConfigManager configManager = new ConfigManager();

            String strHost = configManager.getUndertowHost();
            int intPort = Integer.parseInt(configManager.getUndertowPort());
            String BASE_URL = configManager.getBasePathUrl();

            PathHandler pathHandler = Handlers.path()
                    .addPrefixPath(BASE_URL + "/students", RoutesHandler.students())
                    .addPrefixPath(BASE_URL + "/teachers", RoutesHandler.teachers())
                    .addPrefixPath(BASE_URL + "/subjects", RoutesHandler.subjects())
                    .addPrefixPath(BASE_URL + "/exams", RoutesHandler.exams())
                    .addPrefixPath(BASE_URL + "/guardians", RoutesHandler.guardians())
                    .addPrefixPath(BASE_URL + "/questions", RoutesHandler.questions())
                    .addPrefixPath(BASE_URL + "/answers", RoutesHandler.answers())
                    .addPrefixPath(BASE_URL + "/grades", RoutesHandler.grades())
                    .addPrefixPath(BASE_URL + "/multiple_choices", RoutesHandler.multiple_choices());


            Undertow server = Undertow.builder()
                    .setServerOption(UndertowOptions.DECODE_URL, true)
                    .setServerOption(UndertowOptions.URL_CHARSET, StandardCharsets.UTF_8.name())
                    .setServerOption(UndertowOptions.ENABLE_HTTP2, true)
                    .addHttpListener(intPort, strHost)
                    .setHandler(pathHandler)
                    .build();

            server.start();

            System.out.println("Server Started at " + strHost + ":" + intPort);
        } catch (Exception e) {
            System.err.println("Failed to Start REST API");
            e.printStackTrace();
        }
    }
}
