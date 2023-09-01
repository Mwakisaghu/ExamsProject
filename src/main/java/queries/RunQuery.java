package queries;

import java.sql.*;

public class RunQuery {
    private Connection connection;

    public RunQuery( String driverClass, String CONNECTION_URL, String USERNAME, String PASSWORD) {
        try {
            // Load the JDBC driver class
            Class.forName(driverClass);

            // Establish a database connection
            connection = DriverManager.getConnection(CONNECTION_URL, USERNAME, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void selectQuery (String sqlQuery) throws SQLException {
        // Create a Statement
        Statement statement = connection.createStatement();

        // Execute a query
        sqlQuery = "SELECT * FROM students";
        ResultSet resultSet = statement.executeQuery(sqlQuery);

        // Process the query results
        while (resultSet.next()) {
            // Retrieve data from the result set
            String column1Value = resultSet.getString("first_name");
            String column2Value = resultSet.getString("last_name");

            // Repeat for other columns
            System.out.println("first_name: " + column1Value);
            System.out.println("last_name: " + column2Value);
        }
    }

    public void examsQuery(String sqlQuery1) throws SQLException {
        Statement st = connection.createStatement();

        sqlQuery1 = "SELECT\n" +
                "    CONCAT(t.first_name, ',', t.last_name) AS teachers_name,\n" +
                "    e.exam_id,\n" +
                "    e.exam_name\n" +
                "FROM\n" +
                "    exams e\n" +
                "JOIN\n" +
                "    teachers t ON e.teacher_id = t.teacher_id\n" +
                "WHERE\n" +
                "    e.teacher_id = 1";

        ResultSet rs = st.executeQuery(sqlQuery1);

        System.out.println(
                "TASK 2)");

        System.out.println(
                "exam_id || teachers_name || exam_name");
        System.out.println(
                ".........................................."
        );
        while (rs.next()) {
            System.out.println(rs.getString(2) + "\t\t"
                    + rs.getString(1)
                    + "\t\t"
                    + rs.getString(3));
        }

    }

    public void answersQuery(String sqlQuery2) throws SQLException {
        Statement st = connection.createStatement();


        sqlQuery2 = "SELECT\n" +
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
                "    a.student_id = 44\n" +
                "GROUP BY\n" +
                "    a.student_id, a.answer_id, m.multiple_choice_text";

        ResultSet rs = st.executeQuery(sqlQuery2);


        System.out.println(
                "TASK 3)");
        System.out.println(
                "student_name || answer_id || student_id || multiple_choice_text || percentage_score");
        System.out.println(
                "....................................................................................."
        );
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t\t"
                    + rs.getString(2)
                    + "\t\t"
                    + rs.getString(3)
                    + "\t\t"
                    + rs.getString(4)
                    + "\t\t"
                    + rs.getString(5));
        }
    }

    public void rankingsQuery(String sqlQuery3) throws SQLException {
        Statement st = connection.createStatement();

        sqlQuery3 = "SELECT\n" +
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
                "    e.exam_id = 6\n" +
                "ORDER BY\n" +
                "    a.points_accrued DESC\n" +
                "LIMIT\n" +
                "    5";

        ResultSet rs = st.executeQuery(sqlQuery3);

        System.out.println(
                "TASK 4)");
        System.out.println(
                "student_name || exam_name || percentage_score ");
        System.out.println(
                ".............................................................................."
        );
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t\t"
                    + rs.getString(2)
                    + "\t\t"
                    + rs.getString(3));
        }
    }

    public void reportSheetQuery (String sqlQuery4) throws SQLException {
        Statement st = connection.createStatement();

        sqlQuery4 = "SELECT\n" +
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

        ResultSet rs = st.executeQuery(sqlQuery4);

        System.out.println(
                "TASK 5)");
        System.out.println(
                "student_id || student_name || average_score ");
        System.out.println(
                "..........................................................................."
        );
        while (rs.next()) {
            System.out.println(rs.getString(1) + "\t\t"
                    + rs.getString(2)
                    + "\t\t"
                    + rs.getString(3));
        }
        rs.close();
        st.close();
        connection.close();
    }

    public void addBatchQuery (String SQL ) throws SQLException {
        // Create a statement
        Statement st = connection.createStatement();

        //Setting connection to auto false
        connection.setAutoCommit(false);

        //Creating  sql statement (Insert)
        SQL = "INSERT INTO students (student_id, first_name, middle_name, last_name, class_tier_id, date_of_birth, gender, email_address, created_at) " +
                "VALUES (55, 'Facundo', 'Pele', 'Pellistri', 4, '2003-08-11', 'Male', 'f@example90.com', current_timestamp)";
        st.addBatch(SQL);

        SQL = "INSERT INTO students (student_id,first_name, middle_name, last_name, class_tier_id, date_of_birth, gender,email_address, created_at) " +
                "VALUES (56, 'Donny', 'VanDer', 'Beek', 5, '1997-09-10', 'Male', 'dv@example.com', current_timestamp)";
        st.addBatch(SQL);

        //Update
        SQL = "UPDATE students SET date_of_birth = '1994-10-15' " +
                "WHERE student_id = 37";
        st.addBatch(SQL);

        // int[] to hold the values
        int[] count = st.executeBatch();

        //commit statement - apply changes
        connection.commit();

        //Check if successful
        System.out.println(
                "........................................................................" +
                        "Insert, Update , Delete is successful!!"
        );

        st.close();
        connection.close();
    }
}
