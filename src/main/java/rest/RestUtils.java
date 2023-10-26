package rest;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.URLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class RestUtils {

    // Method to get an endpoint path variable
    public static String getPathVar(HttpServerExchange exchange, String pathVarId) {

        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        StringBuilder builder = new StringBuilder();

        if(pathMatch.getParameters().get(pathVarId)==null){
            return null;
        }

        URLUtils.decode(pathMatch.getParameters().get(pathVarId), StandardCharsets.UTF_8.name(), true, builder);
        return builder.toString();
    }

    // Method to get the request body
    public static String getRequestBody(HttpServerExchange exchange) {
        BufferedReader reader = null;
        StringBuilder builder = new StringBuilder();

        try {
            exchange.startBlocking();
            reader = new BufferedReader(new InputStreamReader(exchange.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }
}


