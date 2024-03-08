package encryption;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptConfigsXml {
    private static SecretKey secretKey;
    private static IvParameterSpec ivParameterSpec;

    public static SecretKey getSecretKey() {
        return secretKey;
    }

    public static void setSecretKey(SecretKey secretKey) {
        EncryptConfigsXml.secretKey = secretKey;
    }

    public static IvParameterSpec getIvParameterSpec() {
        return ivParameterSpec;
    }

    public static void setIvParameterSpec(IvParameterSpec ivParameterSpec) {
        EncryptConfigsXml.ivParameterSpec = ivParameterSpec;
    }

    public static String encrypt(String data, SecretKey secretKey, IvParameterSpec ivParameterSpec)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);

        byte[] encryptedData = cipher.doFinal(data.getBytes());

        // Encode the encrypted bytes to a base64 string
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    public static String decrypt(String encryptedData, SecretKey secretKey, IvParameterSpec ivParameterSpec)
            throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        // Decode the base64 string to get the encrypted bytes
        byte[] dataBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(dataBytes);

        return new String(decryptedData);
    }

    public static SecretKey generateSecretKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256, new SecureRandom());
        return keyGenerator.generateKey();
    }

    public static IvParameterSpec generateIvParameterSpec() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }
}
