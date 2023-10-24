package queries;

import java.sql.*;
import java.util.Map;

public class QueryManager {
    private static Connection connection;

    public QueryManager(Connection connection) {
        this.connection = connection;
    }

    // Method to execute a SELECT Query with params
    public static ResultSet executeSelectQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeQuery();
    }

    // Method to execute an UPDATE query with parameters
    public static int executeUpdateQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeUpdate();
    }

    // Method to execute an INSERT query with parameters and return the generated keys
    public static ResultSet executeInsertQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        preparedStatement.executeUpdate();
        return preparedStatement.getGeneratedKeys();
    }

    // Method to execute a DELETE query with parameters
    public static int executeDeleteQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
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