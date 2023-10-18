package queries;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

public class QueryManager {
    private Connection connection;

    public QueryManager(Connection connection) {
        this.connection = connection;
    }

    // Method to execute a SELECT Query with params
    public ResultSet executeSelectQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // params for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeQuery();
    }

    // Method to execute an UPDATE query with params
    public int executeUpdateQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        return preparedStatement.executeUpdate();
    }

    // Method to execute an INSERT query with params & return the generated keys
    public ResultSet executeInsertQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, PreparedStatement.RETURN_GENERATED_KEYS);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(entry.getKey(), entry.getValue());
        }

        preparedStatement.executeUpdate();
        return preparedStatement.getGeneratedKeys();
    }

    // Method to execute a DELETE query
    public int executeDeleteQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
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
