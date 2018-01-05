package dk.nodes.locksmith.utils;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import dk.nodes.locksmith.BuildConfig;

public class HashingUtils {
    public static String doHash(String text, String CHAR_SET) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] result = digest.digest(text.getBytes(Charset.forName(CHAR_SET)));

            StringBuilder sb = new StringBuilder();

            for (byte b : result) {
                sb.append(String.format("%02X", b));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            if (BuildConfig.DEBUG) {
                e.printStackTrace();
            }
            return text;
        }
    }
}
