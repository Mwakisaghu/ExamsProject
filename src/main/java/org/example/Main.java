package org.example;

import data.ConfigReader;
import queries.RunQuery;

public class Main {
    public static void main(String[] args) {
        ConfigReader cReader = new ConfigReader();

        try {
            String DRIVER_CLASS = cReader.getDriverClass();
            String CONNECTION_URL = cReader.getConnectionURL();
            String USERNAME = cReader.getUsername();
            String PASSWORD = cReader.getPassword();

            RunQuery runQuery = new RunQuery(DRIVER_CLASS, CONNECTION_URL, USERNAME, PASSWORD);

            // Select Test
            String sqlQuery = "SELECT * FROM students";
            runQuery.selectQuery(sqlQuery);

            //Task 2
            String sqlQuery1 = "SELECT * FROM exams";
            runQuery.examsQuery(sqlQuery1);

            //Task 3
            String sqlQuery2 = null;
            runQuery.answersQuery(null);

            //Task 4
            String sqlQuery3 = "SELECT * FROM answers";
            runQuery.rankingsQuery(sqlQuery3);

            //TASK 5
            String sqlQuery4 = "SELECT FROM students";
            runQuery.reportSheetQuery(sqlQuery4);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}