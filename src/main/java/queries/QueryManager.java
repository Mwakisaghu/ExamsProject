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
import java.util.*;

public class QueryManager {
    // Store the database connection for reuse
    public static Connection connection;

    // Establish a database connection
    public static Connection getConnection() throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException {
        // Initialize the ConfigManager - read database configuration
        ConfigManager configManager = new ConfigManager();

        try {
            // Fetch database connection details from the configuration
            String driverClass = configManager.getDriverClass();
            String connectionUrl = configManager.getConnectionURL();
            String username = configManager.getUsername();
            String password = configManager.getPassword();

            // Load the database driver class
            Class.forName(driverClass);

            // Establish a connection to the database
            connection = DriverManager.getConnection(connectionUrl, username, password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException | TransformerException | InvalidKeyException | BadPaddingException |
                 NoSuchAlgorithmException | IllegalBlockSizeException | NoSuchPaddingException |
                 XPathExpressionException e) {
            // Handle exceptions related to database configuration
            throw new RuntimeException(e);
        }
        return connection;
    }

    // Execute a SELECT query and return a list of result maps
    public static List<LinkedHashMap<String, Object>> executeSelectQuery(String sqlQuery, HashMap<Integer, Object> paramMap) {
        // Initialize the results list
        List<LinkedHashMap<String, Object>> results = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            // Setting the parameter values in the prepared statement
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(Integer.parseInt(String.valueOf(entry.getKey())), entry.getValue());
            }

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                ResultSetMetaData metaData = resultSet.getMetaData();
                int count = metaData.getColumnCount();

                // Iterate through the result set , extract data
                while (resultSet.next()) {
                    LinkedHashMap<String, Object> result = new LinkedHashMap<>();
                    for (int i = 1; i <= count; i++) {
                        // Mapping column names to their respective values
                        result.put(metaData.getColumnLabel(i), resultSet.getObject(i));
                    }
                    results.add(result);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handling exceptions
        } catch (ParserConfigurationException | SAXException | NoSuchAlgorithmException | IOException e) {
            throw new RuntimeException(e);
        }
        return results;
    }

    // Execute an INSERT query and return a map of generated keys
    public static HashMap<String, Object> executeInsertQuery(String sqlQuery, Map<Integer, Object> paramMap) {
        // Initialize the result map
        HashMap<String, Object> resultMap = new HashMap<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

            int paramIndex = 1;
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(paramIndex++, entry.getValue());
            }

            int affectedRows = preparedStatement.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    ResultSetMetaData resultSetMetaData = generatedKeys.getMetaData();
                    if (generatedKeys.next()) {
                        int totalColumns = resultSetMetaData.getColumnCount();
                        for (int i = 1; i <= totalColumns; i++) {
                            String columnName = resultSetMetaData.getColumnLabel(i);
                            Object columnValue = generatedKeys.getObject(i);
                            resultMap.put(columnName, columnValue);
                        }
                    }
                }
            }
        } catch (SQLException | ParserConfigurationException | IOException | NoSuchAlgorithmException | SAXException e) {
            e.printStackTrace();
        }
        return resultMap;
    }

    // Execute an UPDATE query and return a map of updated values
    public static Map<String, Object> executeUpdateQuery(String sqlQuery, Map<Integer, Object> paramMap) {
        // Execute an UPDATE query and return the updated rows and additional data as a map

        Map<String, Object> result = new HashMap<>();
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

            // Set parameter values in the prepared statement
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(Integer.parseInt(String.valueOf(entry.getKey())), entry.getValue());
            }

            int updatedRows = preparedStatement.executeUpdate();
            result.put("updated_rows", updatedRows);

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                ResultSetMetaData resultSetMetaData = generatedKeys.getMetaData();
                if (generatedKeys.next()) {
                    int totalColumns = resultSetMetaData.getColumnCount();
                    for (int i = 1; i <= totalColumns; i++) {
                        String columnName = resultSetMetaData.getColumnLabel(i);
                        Object columnValue = generatedKeys.getObject(i);
                        result.put(columnName, columnValue);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle or log any exceptions
        } catch (ParserConfigurationException | IOException | NoSuchAlgorithmException | SAXException e) {
            e.printStackTrace();
        }
        return result;
    }

    // Execute a DELETE query and return the number of deleted rows
    public static int executeDeleteQuery(String sqlQuery, Map<Integer, Object> paramMap) {

        // Initializing the count of deleted rows
        int deletedRows = 0;
        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery)) {

            // Setting parameter values in the prepared statement
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(Integer.parseInt(String.valueOf(entry.getKey())), entry.getValue());
            }


            deletedRows = preparedStatement.executeUpdate();
        } catch (SQLException | ParserConfigurationException | IOException | NoSuchAlgorithmException | SAXException e) {
            e.printStackTrace();
            // Handle or log any exceptions
        }
        return deletedRows;
    }
}
