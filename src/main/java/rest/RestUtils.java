package rest;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.PathTemplateMatch;
import io.undertow.util.URLUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.HashMap;

public class RestUtils {

    // Method to get an endpoint path variable
    public static String getPathVar(HttpServerExchange exchange, String pathVarId) {

        PathTemplateMatch pathMatch = exchange.getAttachment(PathTemplateMatch.ATTACHMENT_KEY);
        StringBuilder builder = new StringBuilder();

        if(pathMatch.getParameters().get(pathVarId)==null){/*HashMap<String, Object> existingUserMap = DatabaseHandler.allUsersList().get(Integer.parseInt(strUserId));*/
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

    public static HashMap<String, String> getQueryParams(HttpServerExchange exchange, String... keys) {

        HashMap<String, String> params = new HashMap<>();
        Deque<String> param = null;

        for (String key : keys) {
            param = exchange.getQueryParameters().get(key);

            if (param != null && !param.getFirst().isEmpty())
            {
                String paramStr = param.getFirst();
                paramStr = URLDecoder.decode(paramStr, StandardCharsets.UTF_8);
                params.put(key, paramStr);

            }
        }

        return params;
    }
}


