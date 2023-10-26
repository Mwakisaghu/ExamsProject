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
}
