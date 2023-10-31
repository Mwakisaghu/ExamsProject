package rest;

import com.google.gson.Gson;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

import java.util.HashMap;

public class FallBack implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) {

        HashMap<String, String> errorMap = new HashMap<>();
        errorMap.put("error_code", "ERR110");
        errorMap.put("error", "URI "+exchange.getRequestURI()+" not found on server");

        Gson gson = new Gson();
        String strJsonResponse = gson.toJson(errorMap);

        exchange.setStatusCode(StatusCodes.NOT_FOUND);
        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
        exchange.getResponseSender().send(strJsonResponse);
    }
}