package dk.nodes.locksmith.core.util;

import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public class HashingUtils {
    private static Charset DEFAULT_CHARSET = Charset.forName("UTF-8");

    public static String sha512(String value) {
        return bytesToHex(hashString("SHA-512", value));
    }

    public static String sha512AsBase64(String value) {
        return bytesToBase64(hashString("SHA-512", value));
    }

    public static byte[] sha256AsByteArray(String value) {
        return hashString("SHA-256", value);
    }

    public static String sha256(String value) {
        return bytesToHex(hashString("SHA-256", value));
    }

    public static String sha256AsBase64(String value) {
        return bytesToBase64(hashString("SHA-256", value));
    }

    public static String sha1(String value) {
        return bytesToHex(hashString("SHA-1", value));
    }

    public static String sha1AsBase64(String value) {
        return bytesToBase64(hashString("SHA-1", value));
    }

    private static byte[] hashString(String type, String value) {
        try {
            byte[] valueBytes = value.getBytes(DEFAULT_CHARSET);

            byte[] digestBytes = MessageDigest
                    .getInstance(type)
                    .digest(valueBytes);

            return digestBytes;
        } catch (NoSuchAlgorithmException e) {
            return value.getBytes(DEFAULT_CHARSET);
        }
    }

    private static String bytesToBase64(byte[] hash) {
        return Base64.encodeToString(hash, Base64.NO_WRAP);
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

    public static String hmacSha256(String password, String value) throws LocksmithException {
        try {
            return hmacSha256(password.getBytes("UTF-8"), value);
        } catch (UnsupportedEncodingException e) {
            throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
        }
    }

    public static String hmacSha256(byte[] password, String value) throws LocksmithException {
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");

            SecretKeySpec secret_key = new SecretKeySpec(password, "HmacSHA256");
            sha256_HMAC.init(secret_key);

            byte[] bytes = sha256_HMAC.doFinal(value.getBytes("UTF-8"));
            return Base64.encodeToString(bytes, Base64.NO_WRAP);
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
        }
    }

}
