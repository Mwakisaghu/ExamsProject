package queries;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class NewQueryRunner {
    private final QueryManager queryManager;

    public NewQueryRunner(Connection connection) throws SQLException, ClassNotFoundException {
        queryManager = new QueryManager(connection);
    }

    public void selectQuery() throws SQLException {
        String selectQuery = "SELECT * FROM students WHERE student_id = ?";

        // parameter map to hold the student ID value
        Map<Integer, Object> paramMap = new HashMap<>();
        paramMap.put(1, 44);
        ResultSet resultSet = null;

        try {
            // Executing the query
            resultSet = queryManager.executeSelectQuery(selectQuery, paramMap);

            while (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                System.out.println("First Name: " + firstName + ", Last Name: " + lastName);
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

    public void examsQuery() throws SQLException {
        String sqlQuery = "SELECT CONCAT(t.first_name, ', ', t.last_name) AS teachers_name, " +
                "e.exam_id, e.exam_name FROM exams e JOIN teachers t ON e.teacher_id = t.teacher_id WHERE e.teacher_id = ?";
        Map<Integer, Object> examsMap = new HashMap<>();
        examsMap.put(1, 1);
        ResultSet resultSet = null;

        try {
            resultSet = queryManager.executeSelectQuery(sqlQuery, examsMap);

            System.out.println("TASK 2)");
            System.out.println("exam_id || teachers_name || exam_name");
            System.out.println("..........................................");

            while (resultSet.next()) {
                String examId = resultSet.getString("exam_id");
                String teachersName = resultSet.getString("teachers_name");
                String examName = resultSet.getString("exam_name");

                System.out.println(examId + "\t\t" + teachersName + "\t\t" + examName);
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

    public void answersQuery() throws SQLException {
        String sqlQuery2 = "SELECT\n" +
                "    CONCAT(s.first_name, ', ', s.last_name) AS student_name,\n" +
                "    a.answer_id,\n" +
                "    a.student_id,\n" +
                "    m.multiple_choice_text,\n" +
                "    CONCAT(\n" +
                "        ROUND((SUM(a.points_accrued) / (COUNT(*) * 100)) * 100),\n" +
                "        '%'\n" +
                "    ) AS percentage_score\n" +
                "FROM\n" +
                "    answers a\n" +
                "JOIN\n" +
                "    students s ON a.student_id = s.student_id\n" +
                "JOIN\n" +
                "    multiple_choices m ON a.multiple_choice_id = m.multiple_choice_id\n" +
                "WHERE\n" +
                "    a.student_id = ? \n" +
                "GROUP BY\n" +
                "    a.student_id, a.answer_id, m.multiple_choice_text";
        Map<Integer, Object> answersMap = new HashMap<>();
        answersMap.put(1, 44);
        ResultSet resultSet = null;

        try {
            resultSet = queryManager.executeSelectQuery(sqlQuery2, answersMap);

            System.out.println("TASK 3)");
            System.out.println("student_name || answer_id || student_id || multiple_choice_text || percentage_score");
            System.out.println("..............................................................................");

            while (resultSet.next()) {
                String studentName = resultSet.getString("student_name");
                String answerId = resultSet.getString("answer_id");
                String studentId = resultSet.getString("student_id");
                String multipleChoiceText = resultSet.getString("multiple_choice_text");
                String percentageScore = resultSet.getString("percentage_score");

                System.out.println(studentName + "\t\t" + answerId + "\t\t" + studentId + "\t\t" + multipleChoiceText + "\t\t" + percentageScore);
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

    public void rankingsQuery() throws SQLException {
        String sqlQuery3 = "SELECT\n" +
                "    CONCAT(s.first_name, ', ', s.last_name) AS student_name,\n" +
                "    e.exam_name,\n" +
                "    CONCAT(FLOOR(a.points_accrued), '%') AS percentage_score\n" +
                "FROM\n" +
                "    answers a\n" +
                "JOIN\n" +
                "    students s ON a.student_id = s.student_id\n" +
                "JOIN\n" +
                "    multiple_choices m ON a.multiple_choice_id = m.multiple_choice_id\n" +
                "JOIN\n" +
                "    questions q ON m.question_id = q.question_id\n" +
                "JOIN\n" +
                "    exams e ON q.exam_id = e.exam_id\n" +
                "WHERE\n" +
                "    e.exam_id = ?\n" +
                "ORDER BY\n" +
                "    a.points_accrued DESC\n" +
                "LIMIT\n" +
                "    5";
        Map<Integer, Object> rankingsMap = new HashMap<>();
        rankingsMap.put(1, 6);
        ResultSet resultSet = null;

        try {
            resultSet = queryManager.executeSelectQuery(sqlQuery3, rankingsMap);

            System.out.println("TASK 4)");
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

    public void reportSheetQuery() throws SQLException {
        String sqlQuery4 = "SELECT\n" +
                "    s.student_id,\n" +
                "    CONCAT(s.first_name, ' ', s.last_name) AS student_name,\n" +
                "    e.exam_name,\n" +
                "    ROUND(AVG(a.points_accrued), 2) AS average_score\n" +
                "FROM\n" +
                "    students s\n" +
                "JOIN\n" +
                "    answers a ON s.student_id = a.student_id\n" +
                "JOIN\n" +
                "    multiple_choices m ON a.multiple_choice_id = m.multiple_choice_id\n" +
                "JOIN\n" +
                "    questions q ON m.question_id = q.question_id\n" +
                "JOIN\n" +
                "    exams e ON q.exam_id = e.exam_id\n" +
                "GROUP BY\n" +
                "    s.student_id, student_name, e.exam_name\n" +
                "ORDER BY\n" +
                "    average_score DESC";
        Map<Integer, Object> reportSheetMap = new HashMap<>();
        ResultSet resultSet = null;

        try {
            resultSet = queryManager.executeSelectQuery(sqlQuery4, reportSheetMap);

            System.out.println("TASK 5)");
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

    public void insertQuery() throws Exception {
        String insertSql = "INSERT INTO students (student_id, first_name, middle_name, last_name, class_tier_id, date_of_birth, gender, email_address, created_at) " +
                "VALUES (57, 'Altay', 'Bayindir', 'Sub-Gk', 4, '2003-08-11', 'Male', 'aa@example90.com', current_timestamp)";

        Map<Integer, Object> insertSqlMap = new HashMap<>();
        ResultSet resultSet = null;

        try {
            resultSet = queryManager.executeInsertQuery(insertSql, insertSqlMap);

            System.out.println("Insert SQL Query Task)");
            System.out.println("student_id || first_name || last_name || gender || email_address || created_at");
            System.out.println(".........................................................");

            while(resultSet.next()) {
                String studentId = resultSet.getString("student_id");
                String firstName = resultSet.getString("first_name");
                String lastName = resultSet.getString("last_name");
                String Gender = resultSet.getString("gender");
                String emailAddress = resultSet.getString("email_address");
                String createdAt = resultSet.getString("created_at");

                System.out.println(studentId + "\t\t" + firstName + "\t\t" + lastName + "\t\t" + Gender + "\t\t" + emailAddress + "\t\t" + createdAt);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
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

    public void updateQuery() throws  SQLException {
        String updateSql = "UPDATE students SET date_of_birth = '1999-10-15' " +
                "WHERE student_id = ?";

        Map<Integer, Object> updateSqlMap = new HashMap<>();
        updateSqlMap.put(1, 37);

        try {
            queryManager.executeUpdateQuery(updateSql, updateSqlMap);

            System.out.println("Student updated successfully ....");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteQuery() throws SQLException {
        String deleteSql = "DELETE FROM students " +
                "WHERE student_id = ?";

        Map<Integer, Object> deleteSqlMap = new HashMap<>();
        deleteSqlMap.put(1, 57);

        try {
            queryManager.executeDeleteQuery(deleteSql, deleteSqlMap);

            System.out.println("Student with studentId has been successfully deleted");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
