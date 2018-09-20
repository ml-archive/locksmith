package dk.nodes.locksmith.core.util;

import android.util.Base64;

import java.security.SecureRandom;

public class RandomUtils {

    /**
     * @param length is the length in bytes
     * @return a random string according to specifications
     */
    public static String generateRandomString(int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] key = new byte[length];
        secureRandom.nextBytes(key);

        String str = Base64.encodeToString(key, Base64.NO_WRAP);
        return str;
    }
}
