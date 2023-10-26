package queries;

import data.ConfigManager;
import org.xml.sax.SAXException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.Map;

public class QueryManager {
    public static Connection connection;

    public QueryManager() throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException, XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, TransformerException {

        // Initialise
        ConfigManager configManager = new ConfigManager();

        // Retrieve
        String driverClass = configManager.getDriverClass();
        String connectionUrl = configManager.getConnectionURL();
        String username = configManager.getUsername();
        String password = configManager.getPassword();

            try {
                // Load driver
                Class.forName(driverClass);

                // Establishing connection
                connection = DriverManager.getConnection(connectionUrl, username, password);
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
    }

    public static Connection getConnection() {
        return connection;
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
    public static int executeDeleteQuery(String sqlQuery, Map<String, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(Integer.parseInt(entry.getKey()), entry.getValue());
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