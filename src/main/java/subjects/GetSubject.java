package subjects;

import com.google.gson.*;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import queries.QueryManager;
import rest.RestUtils;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static queries.QueryManager.connection;

public class GetSubject implements HttpHandler {
    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        try {
            // Extracting the TeacherId from the path variable
            String strSubjectId = RestUtils.getPathVar(exchange, "subjectId");

            // Convert String subjectId to an integer
            assert strSubjectId != null;
            int subjectId = Integer.parseInt(strSubjectId);

            // Sql query to retrieve a subjects data
            StringBuilder selectSql = new StringBuilder("SELECT * FROM subjects WHERE subject_id = ?");

            // Creating a param map object
            HashMap<Integer, Object> selectMap = new LinkedHashMap<>();
            selectMap.put(1, subjectId);

            // Sorting & filtering
            Deque<String> sortByParams = exchange.getQueryParameters().get("sort");
            String sortBy = sortByParams != null ? sortByParams.getFirst() : "";

            Deque<String> filterParams = exchange.getQueryParameters().get("filter");
            String filter = filterParams != null ? filterParams.getFirst() : "";

            // Applying sorting
            if (!sortBy.isEmpty()) {
                selectSql.append(" ORDER BY ").append(sortBy);
            }

            // Applying filtering
            if (!filter.isEmpty()) {
                // Appending the where clause - if the 1st filter criteria
                if(!selectSql.toString().contains("WHERE")) {
                    selectSql.append(" WHERE ");
                } else  {
                    // Appending AND
                    selectSql.append(" AND ");
                }
                selectSql.append(filter);
            }

            //Executing the query using the Query Manager
            List<LinkedHashMap<String, Object>> subjectList = QueryManager.executeSelectQuery(selectSql.toString(),selectMap);

            //
            if (!subjectList.isEmpty()) {
                LinkedHashMap<String, Object> subjectData = subjectList.get(0);

                // Creating a Json response
                Gson gson = new GsonBuilder().registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).create();
                String strJsonResponse = gson.toJson(subjectData);

                // Setting the HTTP response status code to 200 - OK
                exchange.setStatusCode(StatusCodes.OK);

                // Setting the response content-type
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

                // Sending the json response to the client
                exchange.getResponseSender().send(strJsonResponse);
            } else {
                // Teacher not found
                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                exchange.getResponseSender().send("Error: Teacher not found");
            }
        } catch (NumberFormatException e) {
            // Handle the case where an invalid teacherId is provided
            exchange.setStatusCode(StatusCodes.BAD_REQUEST);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Invalid teacherId");
        } catch (Exception e) {
            // Handle other errors
            e.printStackTrace();
            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
            exchange.getResponseSender().send("Error: Internal Server Error");
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return context.serialize(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
    }
}
