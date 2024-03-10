package auth;

import data.ConfigManager;

import java.util.HashMap;

public class UserAccountsManager {
    private static final int MAX_ATTEMPTS = 3;
    private static final long LOCKOUT_TIME = 240000;
    private static final HashMap<String, Integer> attempts = new HashMap<>();
    private static final HashMap<String, Long> lockout = new HashMap<>();

    public static boolean authenticate(String username, String password) {
        try {
            // Retrieve username and password from the configuration file
            ConfigManager configManager = new ConfigManager();
            String storedUsername = configManager.getUsername();
            String storedPassword = configManager.getPassword();

            // Checking if the user is already locked out
            if (isUserLockedOut(username)) {
                return false;
            }

            // Checking if the user has exceeded maximum login attempts
            if (hasExceededMaxAttempts(username)) {
                lockoutUser(username);
                // User is locked out
                return false;
            }

            // Authenticate user
            boolean authenticated = storedUsername.equals(username) && storedPassword.equals(password);

            // Updating attempts count & generating token on successful authentication
            if (authenticated) {
                resetLoginAttempts(username);
                String token = TokenManager.generateToken(username); // Pass the username here
                System.out.println("Token generated for user: " + username + " - " + token);
            } else {
                incrementLoginAttempts(username);
            }

            return authenticated;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean isUserLockedOut(String username) {
        return lockout.containsKey(username) && lockout.get(username) > System.currentTimeMillis();
    }

    private static boolean hasExceededMaxAttempts(String username) {
        return attempts.containsKey(username) && attempts.get(username) >= MAX_ATTEMPTS;
    }

    private static void lockoutUser(String username) {
        lockout.put(username, System.currentTimeMillis() + LOCKOUT_TIME);
        attempts.remove(username);
    }

    private static void resetLoginAttempts(String username) {
        attempts.remove(username);
    }

    private static void incrementLoginAttempts(String username) {
        attempts.put(username, attempts.getOrDefault(username, 0) + 1);
    }
}
