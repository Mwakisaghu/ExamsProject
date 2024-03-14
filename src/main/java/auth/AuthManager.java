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

    private static Connection conn;

    static {
        try {
            ConfigManager configManager;
            try {
                configManager = new ConfigManager();
            } catch (SAXException e) {
                throw new RuntimeException(e);
            }

            String dbUrl = configManager.getConnectionURL();
            String dbUser = configManager.getUsername();
            String dbPassword = configManager.getPassword();

            conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);

        } catch (SQLException | ParserConfigurationException | IOException | XPathExpressionException |
                 NoSuchAlgorithmException | InvalidAlgorithmParameterException | NoSuchPaddingException |
                 IllegalBlockSizeException | BadPaddingException | TransformerException e) {
            throw new RuntimeException("Error initializing AuthManager", e);
        } catch (InvalidKeyException e) {
            throw new RuntimeException(e);
        }
    }

    public static String authenticateUser(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        try {
            if (isUserLocked(username)) {
                return null;
            }

            String query = "SELECT id FROM users WHERE username = ? AND password = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                statement.setString(2, password);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        resetLoginAttempts(username);
                        return TokenManager.generateToken(username); // Generate token upon successful authentication
                    } else {
                        trackLoginAttempt(username);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error authenticating user", e);
        }
        return null;
    }

    private static boolean isUserLocked(String username) {
        try {
            String query = "SELECT is_locked FROM users WHERE username = ?";
            try (PreparedStatement statement = conn.prepareStatement(query)) {
                statement.setString(1, username);
                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return resultSet.getBoolean("is_locked");
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error checking user lock status", e);
        }
        return false;
    }

    private static void trackLoginAttempt(String username) {
        try {
            incrementLoginAttempts(username);
            if (getLoginAttemptsCount(username) >= MAX_LOGIN_ATTEMPTS) {
                lockUserAccount(username);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error tracking login attempt", e);
        }
    }

    private static int getLoginAttemptsCount(String username) throws SQLException {
        String query = "SELECT COUNT(*) FROM login_attempts WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }
        return 0;
    }

    private static void incrementLoginAttempts(String username) throws SQLException {
        String query = "INSERT INTO login_attempts (username) VALUES (?)";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    private static void resetLoginAttempts(String username) throws SQLException {
        String query = "DELETE FROM login_attempts WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, username);
            statement.executeUpdate();
        }
    }

    private static void lockUserAccount(String username) throws SQLException {
        long lockedUntil = System.currentTimeMillis() + LOCKOUT_TIME;
        String query = "UPDATE users SET is_locked = true, locked_until = ? WHERE username = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setLong(1, lockedUntil);
            statement.setString(2, username);
            statement.executeUpdate();
        }
    }
}
