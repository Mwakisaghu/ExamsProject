package auth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TokenManager {
    private static final long TOKEN_VALIDITY_DURATION = 600000;
    private static final Map<String, TokenInfo> tokenMap = new HashMap<>();

    public static String generateToken(String username) {
        String token = UUID.randomUUID().toString();
        long expirationTime = System.currentTimeMillis() + TOKEN_VALIDITY_DURATION;
        TokenInfo tokenInfo = new TokenInfo(username, expirationTime);
        tokenMap.put(token, tokenInfo);
        return token;
    }

    public static boolean validateToken(String token) {
        TokenInfo tokenInfo = tokenMap.get(token);
        return tokenInfo != null && tokenInfo.getExpirationTime() > System.currentTimeMillis();
    }

    public static String refreshToken(String token) {
        TokenInfo tokenInfo = tokenMap.get(token);
        if (tokenInfo != null && tokenInfo.getExpirationTime() > System.currentTimeMillis()) {
            tokenInfo.setExpirationTime(System.currentTimeMillis() + TOKEN_VALIDITY_DURATION);
            return token;
        } else {
            tokenMap.remove(token);
            return null;
        }
    }

    public static TokenInfo getTokenInfo(String token) {
        return tokenMap.get(token);
    }

    public static long getTokenValidityDuration() {
        return TOKEN_VALIDITY_DURATION;
    }

    public static class TokenInfo {
        private String username;
        private long expirationTime;

        public TokenInfo(String username, long expirationTime) {
            this.username = username;
            this.expirationTime = expirationTime;
        }

        public long getExpirationTime() {
            return expirationTime;
        }

        public void setExpirationTime(long expirationTime) {
            this.expirationTime = expirationTime;
        }
    }
}
