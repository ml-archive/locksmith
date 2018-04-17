package dk.nodes.locksmith.core.util;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashingUtils {
    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static String sha512(String value) {
        return hashString("SHA-512", value);
    }

    public static String sha256(String value) {
        return hashString("SHA-256", value);
    }

    public static String sha1(String value) {
        return hashString("SHA-1", value);
    }

    private static String hashString(String type, String value) {
        try {
            byte[] valueBytes = value.getBytes(DEFAULT_CHARSET);

            byte[] digestBytes = MessageDigest
                    .getInstance(type)
                    .digest(valueBytes);

            return bytesToHex(digestBytes);
        } catch (NoSuchAlgorithmException e) {
            return value;
        }
    }

    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();

        for (byte aHash : hash) {
            String hex = Integer.toHexString(0xff & aHash);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }
}
