package users;

import data.ConfigManager;

public class UserAccountsManager {
    public static boolean authenticate(String username, String password) {
        try {
            ConfigManager configManager = new ConfigManager();
            String retrievedUsername = configManager.getUsername();
            String retrievedPassword = configManager.getPassword();

            // Checking if username and password match
            return  username.equals(retrievedUsername) && password.equals(retrievedPassword);

        } catch (Exception e) {
            e.printStackTrace();
            return  false;
        }
    }
}
