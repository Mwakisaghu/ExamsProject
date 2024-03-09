package org.example;

import auth.AuthFilter;
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

            // updates config xml with encrypted values
            configManager.updateConfig();

            // Initialising the auth filter
            AuthFilter authFilter = new AuthFilter();

            PathHandler pathHandler = Handlers.path()
                    .addPrefixPath(BASE_URL + "/students", authFilter.doFilter(RoutesHandler.students()))
                    .addPrefixPath(BASE_URL + "/teachers", authFilter.doFilter(RoutesHandler.teachers()))
                    .addPrefixPath(BASE_URL + "/subjects", authFilter.doFilter(RoutesHandler.subjects()))
                    .addPrefixPath(BASE_URL + "/exams", authFilter.doFilter(RoutesHandler.exams()))
                    .addPrefixPath(BASE_URL + "/guardians", authFilter.doFilter(RoutesHandler.guardians()))
                    .addPrefixPath(BASE_URL + "/questions", authFilter.doFilter(RoutesHandler.questions()))
                    .addPrefixPath(BASE_URL + "/answers", authFilter.doFilter(RoutesHandler.answers()))
                    .addPrefixPath(BASE_URL + "/grades", authFilter.doFilter(RoutesHandler.grades()))
                    .addPrefixPath(BASE_URL + "/multiple_choices", authFilter.doFilter(RoutesHandler.multiple_choices()))
                    .addPrefixPath(BASE_URL + "/class_tiers", authFilter.doFilter(RoutesHandler.class_tiers()));

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
