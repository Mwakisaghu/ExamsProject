package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class QueryManager {
    private static Connection connection;

    public QueryManager(Connection connection) {

        this.connection = connection;
    }

 students
    // Method to execute a SELECT Query with params

    // Method to execute a SELECT query with params
 main
    public static ResultSet executeSelectQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeQuery();
    }

 students
    // Method to execute an UPDATE query with parameters

    // Method to execute an UPDATE query with params
main
    public static int executeUpdateQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeUpdate();
    }

 students
    // Method to execute an INSERT query with parameters and return the generated keys
    public static ResultSet executeInsertQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

    // Method to execute an INSERT query with params & return the generated keys
    public static int executeInsertQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS);
 main

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        int rowCount = preparedStatement.executeUpdate();

        return rowCount;
    }

 students
    // Method to execute a DELETE query with parameters

    // Method to execute a DELETE query
 main
    public static int executeDeleteQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // params for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeUpdate();
    }

    // Close the connection
    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }
}
