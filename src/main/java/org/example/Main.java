package org.example;

import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.UndertowOptions;
import io.undertow.server.handlers.PathHandler;
import queries.QueryManager;
import routes.RoutesHandler;

import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) {
        System.out.println("Starting REST API");

        try {
            QueryManager queryManager = new QueryManager();

            String strHost = "localhost";
            int intPort = 7080;
            String BASE_URL = "/api";

            PathHandler pathHandler = Handlers.path()
                    .addPrefixPath(BASE_URL + "/students", RoutesHandler.configureRoutes(QueryManager.getConnection()));


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
