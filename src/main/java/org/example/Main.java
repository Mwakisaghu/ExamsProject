package org.example;

import data.ConfigManager;
import org.xml.sax.SAXException;
import queries.QueryRunner;

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
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {
        try {
            // Initialize the ConfigManager
            ConfigManager configManager = new ConfigManager();

            // Reading the database connection details from the XML configs
            String driverClass = configManager.getDriverClass();
            String connectionUrl = configManager.getConnectionURL();
            String dbName = configManager.getDatabaseName();
            String decryptedUsername = configManager.getUsername();
            String decryptedPassword = configManager.getPassword();


            // Load the database driver class
            Class.forName(driverClass);

            try (Connection connection = DriverManager.getConnection(connectionUrl, decryptedUsername, decryptedPassword)) {

                QueryRunner queryRunner = new QueryRunner(connection);

                // Run your queries here using QueryRunner methods
                QueryRunner.selectQuery();
                QueryRunner.examsQuery();
                QueryRunner.answersQuery();
                QueryRunner.rankingsQuery();
                QueryRunner.reportSheetQuery();
                QueryRunner.insertQuery();
                QueryRunner.updateQuery();
                QueryRunner.deleteQuery();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException | XPathExpressionException | IllegalBlockSizeException |
                 NoSuchPaddingException | ParserConfigurationException | IOException | NoSuchAlgorithmException |
                 BadPaddingException | InvalidKeyException | TransformerException | SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
