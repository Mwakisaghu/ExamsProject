/*
package queries;

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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class QueryRunner {
    private static QueryManager queryManager = null;

    public QueryRunner(Connection connection) throws SQLException, ClassNotFoundException, InvalidAlgorithmParameterException, XPathExpressionException, NoSuchPaddingException, IllegalBlockSizeException, ParserConfigurationException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException, TransformerException, SAXException {
        queryManager = new QueryManager(connection);
    }

    public static void selectQuery() throws SQLException {
        String selectQuery = "SELECT * FROM students WHERE student_id = ?";
        Map<Integer, Object> paramMap = new HashMap<>();
        paramMap.put(1, 44);

        ResultSet resultSet = null;

        try {
            resultSet = QueryManager.executeSelectQuery(selectQuery, paramMap);
            System.out.println("Results from selectQuery:");
            System.out.println("student_id || first_name || last_name || gender");
            System.out.println("............................................");

            while (resultSet.next()) {
                String studentIdResult = resultSet.getString("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String gender = resultSet.getString("gender");

                System.out.println(studentIdResult + "\t\t" + firstName + "\t\t" + lastName + "\t\t" + gender);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void examsQuery() throws SQLException {
        String sqlQuery = "SELECT CONCAT(t.first_name, ', ', t.last_name) AS teachers_name, " +
                "e.exam_id, e.exam_name FROM exams e JOIN teachers t ON e.teacher_id = t.teacher_id WHERE e.teacher_id = ?";
        Map<Integer, Object> examsMap = new HashMap<>();
        examsMap.put(1, 1);

        ResultSet resultSet = null;

        try {
            resultSet = QueryManager.executeSelectQuery(sqlQuery, examsMap);
            System.out.println("Results from examsQuery:");
            System.out.println("teachers_name || exam_id || exam_name");
            System.out.println("........................................");

            while (resultSet.next()) {
                String teachersName = resultSet.getString("teachers_name");
                String examId = resultSet.getString("exam_id");
                String examName = resultSet.getString("exam_name");

                System.out.println(teachersName + "\t\t" + examId + "\t\t" + examName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void answersQuery() throws SQLException {
        String sqlQuery = "SELECT " +
                "CONCAT(s.first_name, ', ', s.last_name) AS student_name, " +
                "a.answer_id, a.student_id, " +
                "m.multiple_choice_text, " +
                "CONCAT(ROUND((SUM(a.points_accrued) / (COUNT(*) * 100)) * 100), '%') AS percentage_score " +
                "FROM answers a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN multiple_choices m ON a.multiple_choice_id = m.multiple_choice_id " +
                "WHERE a.student_id = ? " +
                "GROUP BY a.student_id, a.answer_id, m.multiple_choice_text";

        Map<Integer, Object> answersMap = new HashMap<>();
        answersMap.put(1, 44);

        ResultSet resultSet = null;

        try {
            resultSet = QueryManager.executeSelectQuery(sqlQuery, answersMap);
            System.out.println("Results from answersQuery:");
            System.out.println("student_name || answer_id || student_id || multiple_choice_text || percentage_score");
            System.out.println("..............................................................................");

            while (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                String answerId = resultSet.getString("answer_id");
                String studentIdResult = resultSet.getString("student_id");
                String multipleChoiceText = resultSet.getString("multiple_choice_text");
                String percentageScore = resultSet.getString("percentage_score");

                System.out.println(studentName + "\t\t" + answerId + "\t\t" + studentIdResult + "\t\t" + multipleChoiceText + "\t\t" + percentageScore);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void rankingsQuery() throws SQLException {
        String sqlQuery = "SELECT " +
                "CONCAT(s.first_name, ', ', s.last_name) AS student_name, " +
                "e.exam_name, " +
                "CONCAT(FLOOR(a.points_accrued), '%') AS percentage_score " +
                "FROM answers a " +
                "JOIN students s ON a.student_id = s.student_id " +
                "JOIN multiple_choices m ON a.multiple_choice_id = m.multiple_choice_id " +
                "JOIN questions q ON m.question_id = q.question_id " +
                "JOIN exams e ON q.exam_id = e.exam_id " +
                "WHERE e.exam_id = ? " +
                "ORDER BY a.points_accrued DESC " +
                "LIMIT 5";

        Map<Integer, Object> rankingsMap = new HashMap<>();
        rankingsMap.put(1, 6);

        ResultSet resultSet = null;

        try {
            resultSet = QueryManager.executeSelectQuery(sqlQuery, rankingsMap);
            System.out.println("Results from rankingsQuery:");
            System.out.println("student_name || exam_name || percentage_score");
            System.out.println("..............................................");

            while (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                String examName = resultSet.getString("exam_name");
                String percentageScore = resultSet.getString("percentage_score");

                System.out.println(studentName + "\t\t" + examName + "\t\t" + percentageScore);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void reportSheetQuery() throws SQLException {
        String sqlQuery = "SELECT " +
                "s.student_id, " +
                "CONCAT(s.first_name, ' ', s.last_name) AS student_name, " +
                "e.exam_name, " +
                "ROUND(AVG(a.points_accrued), 2) AS average_score " +
                "FROM students s " +
                "JOIN answers a ON s.student_id = a.student_id " +
                "JOIN multiple_choices m ON a.multiple_choice_id = m.multiple_choice_id " +
                "JOIN questions q ON m.question_id = q.question_id " +
                "JOIN exams e ON q.exam_id = e.exam_id " +
                "GROUP BY s.student_id, student_name, e.exam_name " +
                "ORDER BY average_score DESC";

        Map<Integer, Object> reportSheetMap = new HashMap<>();
        ResultSet resultSet = null;

        try {
            resultSet = QueryManager.executeSelectQuery(sqlQuery, reportSheetMap);
            System.out.println("Results from reportSheetQuery:");
            System.out.println("student_id || student_name || exam_name || average_score");
            System.out.println(".........................................................");

            while (resultSet.next()) {
                String studentId = resultSet.getString("student_id");
                String studentName = resultSet.getString("student_name");
                String examName = resultSet.getString("exam_name");
                String averageScore = resultSet.getString("average_score");

                System.out.println(studentId + "\t\t" + studentName + "\t\t" + examName + "\t\t" + averageScore);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void insertQuery() throws SQLException {
        String insertSql = "INSERT INTO students (student_id, first_name, middle_name, last_name, class_tier_id, date_of_birth, gender, email_address, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, current_timestamp)";

        Map<Integer, Object> insertSqlMap = new HashMap<>();
        insertSqlMap.put(1, 57);
        insertSqlMap.put(2, "Altay");
        insertSqlMap.put(3, "Bayindir");
        insertSqlMap.put(4, "Sub-Gk");
        insertSqlMap.put(5, 4);
        insertSqlMap.put(6, "2003-08-11");
        insertSqlMap.put(7, "Male");
        insertSqlMap.put(8, "aa@example90.com");

        try {
            QueryManager.executeInsertQuery(insertSql, insertSqlMap);
            System.out.println("Student inserted successfully.");
        } catch (SQLException e) {
            System.err.println("Failed to insert student: " + e.getMessage());
        }
    }
    public static void updateQuery() throws SQLException {
        String updateSql = "UPDATE students SET date_of_birth = ? WHERE student_id = ?";
        Map<Integer, Object> updateSqlMap = new HashMap<>();
        updateSqlMap.put(1, "2003-08-11");
        updateSqlMap.put(2, 37);

        try {
            QueryManager.executeUpdateQuery(updateSql, updateSqlMap);
            System.out.println("Student with studentId 37 has been successfully updated.");
        } catch (SQLException e) {
            System.err.println("Failed to update the student: " + e.getMessage());
        }
    }
    public static void deleteQuery() throws SQLException {
        String deleteSql = "DELETE FROM students WHERE student_id = ?";
        Map<Integer, Object> deleteSqlMap = new HashMap<>();
        deleteSqlMap.put(1, 57);

        try {
            int rowCount = QueryManager.executeDeleteQuery(deleteSql, deleteSqlMap);

            if (rowCount > 0) {
                System.out.println("Student with studentId 57 has been successfully deleted.");
            } else {
                System.out.println("No student with studentId 57 found for deletion.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // close connections
    public static void close() throws SQLException {
        queryManager.closeConnection();
    }
}




























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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryManager {
    public static Connection connection;
    public QueryManager() throws ParserConfigurationException, IOException, NoSuchAlgorithmException, SAXException,
            XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException, TransformerException {

    }
    public static Connection getConnection() {
        try {
            ConfigManager configManager = new ConfigManager();

            // Retrieve
            String driverClass = configManager.getDriverClass();
            String connectionUrl = configManager.getConnectionURL();
            String username = configManager.getUsername();
            String password = configManager.getPassword();

            // Load driver
            Class.forName(driverClass);

            // Establishing connection
            connection = DriverManager.getConnection(connectionUrl, username, password);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return connection;
    }

    // Method to execute a SELECT Query with params
    public static List<HashMap<String, Object>> executeSelectQuery(Connection connection, String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        HashMap<String, Object> selectMap = new HashMap<>();

        try {
            preparedStatement = connection.prepareStatement(sqlQuery);

            // Set parameters for the prepared statement
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(entry.getKey(), entry.getValue());
            }

            resultSet = preparedStatement.executeQuery();

            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            if (resultSet.next()) {
                int totalColumns = resultSetMetaData.getColumnCount();
                for (int i =1 ; i <= totalColumns; i++) {
                    selectMap.put(resultSetMetaData.getColumnLabel(i), resultSet.getString(i));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return resultMap;
    }

    // Method to execute an INSERT query with parameters and return the generated keys
    public static HashMap<String, Object> executeInsertQuery(Connection connection, String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        HashMap<String, Object> resultMap = new HashMap<>();

        try {
            preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            // Set parameters for the prepared statement
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(Integer.parseInt(String.valueOf(entry.getKey())), entry.getValue());
            }

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Object lastInsertVar = resultSet.getObject(1);

                // SQL query to retrieve student data by studentId
                String selectQuery = "SELECT * FROM students WHERE student_id = ?";

                // Creating a parameter map for the query
                Map<Integer, Object> selectMap = new HashMap<>();
                selectMap.put(1, lastInsertVar);
                resultMap = executeSelectQuery(connection, selectQuery, selectMap);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return resultMap;
    }


    // Method to execute an UPDATE query with parameters
    public static HashMap<String, Object> executeUpdateQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        HashMap<String, Object> updateMap = new HashMap<>();

        try {
            preparedStatement = connection.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS);

            // Set parameters for the prepared statement
            for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
                preparedStatement.setObject(Integer.parseInt(String.valueOf(entry.getKey())), entry.getValue());
            }

            preparedStatement.executeUpdate();

            resultSet = preparedStatement.getGeneratedKeys();
            resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                Object lastInsertVar = resultSet.getObject(1);

                // SQL query to retrieve student data by studentId
                String selectQuery = "SELECT * FROM students WHERE student_id = ?";

                // Creating a parameter map for the query
                Map<Integer, Object> selectMap = new HashMap<>();
                selectMap.put(1, lastInsertVar);
                updateMap = executeSelectQuery(connection, selectQuery, updateMap);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return updateMap;
    }


    // Method to execute a DELETE query with parameters
    public static int executeDeleteQuery(String sqlQuery, Map<Integer, Object> paramMap) throws SQLException {
        PreparedStatement preparedStatement = connection.prepareStatement(sqlQuery);

        // Set parameters for the prepared statement
        for (Map.Entry<Integer, Object> entry : paramMap.entrySet()) {
            preparedStatement.setObject(Integer.parseInt(String.valueOf(entry.getKey())), entry.getValue());
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
*/
