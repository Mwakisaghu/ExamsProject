package auth;

import java.util.HashMap;
import java.util.UUID;

public class TokeManager {
    private static final long TOKEN_VALIDITY_DURATION = 600000;
    private static final HashMap<String, TokenInfo> tokenMap = new Hashmap<>();

    // Token Generation
    public static String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + TOKEN_VALIDITY_DURATION;
        Tokeninfo tokeninfo = new TokeInfo(username, expirationTime);
        tokenMap.put(token, tokeniInfo);
        return  token;
    }

    // Token validation
    public static boolean validateToken(String token) {
        Token tokenInfo = tokenMap.get(token);
        if(tokenInfo != null && tokenInfo.getExpirationTime() > System.currentTimeMillis()) {
            // Ensures token is valid
            return true;
        } else {
            // Removing the expired token
            tokenMap.remove((token));
            // If token is invalid
            return  false;
        }
    }
}
