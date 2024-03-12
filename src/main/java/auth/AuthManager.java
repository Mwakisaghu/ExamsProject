package auth;

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

public class AuthManager {
    private static final int MAX_LOGIN_ATTEMPTS = 3;
    private static final long LOCKOUT_TIME = 5 * 60 * 1000;

    public static String authenticateUser(String username, String password) throws ParserConfigurationException, IOException,
            NoSuchAlgorithmException, XPathExpressionException, InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, TransformerException, SAXException {
        ConfigManager configManager = new ConfigManager();

        String dbUrl = configManager.getConnectionURL();
        String dbUser = configManager.getUsername();
        String dbPassword = configManager.getPassword();

        if (TokenManager.isUserLocked(username)) {
            // Account locked
            return null;
        }

        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword)) {
            // Check if the user exists and the password matches
            String query = "SELECT id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        // User authenticated, reset login attempts and return an access token
                        resetLoginAttempts(conn, username);
                        return TokenManager.generateToken(username);
                    } else {
                        // Invalid credentials, track login attempt
                        trackLoginAttempt(conn, username);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void trackLoginAttempt(Connection conn, String username) {
        incrementLoginAttempts(conn, username);
        if (getLoginAttemptsCount(conn, username) >= MAX_LOGIN_ATTEMPTS) {
            lockUserAccount(conn, username);
        }
    }

    private static int getLoginAttemptsCount(Connection conn, String username) {
        try {
            String query = "SELECT COUNT(*) FROM login_attempts WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private static void incrementLoginAttempts(Connection conn, String username) {
        try {
            String query = "INSERT INTO login_attempts (username) VALUES (?)";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void resetLoginAttempts(Connection conn, String username) {
        try {
            String query = "DELETE FROM login_attempts WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static void lockUserAccount(Connection conn, String username) {
        try {
            long lockedUntil = System.currentTimeMillis() + LOCKOUT_TIME;
            String query = "UPDATE users SET is_locked = true, locked_until = ? WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setLong(1, lockedUntil);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
