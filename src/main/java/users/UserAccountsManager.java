package users;

import data.ConfigManager;

import java.util.HashMap;

public class UserAccountsManager {
    private static final int MAX_ATTEMPTS = 3;
    private static  final  long LOCKOUT_TIME = 240000;
    private static HashMap<String, Integer> attempts = new HashMap<>();
    private static HashMap<String, Long> lockout = new HashMap<>();

    public static boolean authenticate(String username, String password) {
        try {
            ConfigManager configManager = new ConfigManager();
            String retrievedUsername = configManager.getUsername();
            String retrievedPassword = configManager.getPassword();

            // checking if the user is already locked out
            if (lockout.containsKey(username) && lockout.get(username) >System.currentTimeMillis()) {
                return false;
            }

            // Checking if the user has already exceeded maximum login attempts
            if (attempts.containsKey(username) && attempts.get(username) >= MAX_ATTEMPTS) {
                lockout.put(username, System.currentTimeMillis() + LOCKOUT_TIME);
                attempts.remove(username);
                // returns if user is locked out
                return false;
            }

            // Checking if username and password match ones in config.xml
            boolean authenticated = username.equals(retrievedUsername) && password.equals(retrievedPassword);

            // updating the attempts count
            if (!authenticated) {
                attempts.put(username, attempts.getOrDefault(username, 0) +1);
            } else {
                // Resets the attempts on successful login
                attempts.remove(username);
            }
            return authenticated;

        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }
}
