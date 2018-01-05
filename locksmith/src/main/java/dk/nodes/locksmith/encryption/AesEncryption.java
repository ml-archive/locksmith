package dk.nodes.locksmith.encryption;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dk.nodes.locksmith.exceptions.InvalidEncryptionDataException;
import dk.nodes.locksmith.models.EncryptionData;

public class AesEncryption {
    private static final int AES_BIT_LENGTH = 256;
    private static final int GCM_TAG_LENGTH = 128;
    private static final String AES_KEY_TYPE = "AES";
    private static final String AES_BLOCK_MODE = "GCM";
    private static final String AES_PADDING_TYPE = "NoPadding";
    //mode
    private static final String AES_MODE = AES_KEY_TYPE
            + "/" +
            AES_BLOCK_MODE
            + "/" +
            AES_PADDING_TYPE;
    //General Stuff
    private SecretKeySpec aesKey = null;
    private Charset CHAR_SET = Charset.forName("UTF-8");


    /**
     * Generates and returns the AES key that needs to be provided to use the class
     *
     * @return Returns a Secret Key in an AES Format {@link javax.crypto.spec.SecretKeySpec}
     * @throws NoSuchAlgorithmException If the device does not support the AES an exception will be thrown
     */

    public static SecretKeySpec generateKey() throws NoSuchAlgorithmException {
        byte[] key;

        KeyGenerator keyGen = KeyGenerator.getInstance(AES_KEY_TYPE);
        keyGen.init(AES_BIT_LENGTH);

        SecretKey sKey = keyGen.generateKey();
        key = sKey.getEncoded();

        return new SecretKeySpec(key, "AES");
    }

    /**
     * Instantiate the AES Helper class
     * use {@link AesEncryption#generateKey()} for generating the key
     *
     * @param secretKeySpec Must provide an AES Key {@link javax.crypto.spec.SecretKeySpec}
     */

    public AesEncryption(SecretKeySpec secretKeySpec) {
        this.aesKey = secretKeySpec;
    }

    private byte[] getIV() {
        byte[] iv = new byte[12];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(iv);
        return iv;
    }

    public String encryptString(String unencryptedString) throws NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        byte[] stringBytes = unencryptedString.getBytes(CHAR_SET);
        byte[] ivBytes = getIV();
        return encrypt(stringBytes, ivBytes).encode();
    }

    public String decryptString(String encryptedString) throws InvalidEncryptionDataException, NoSuchPaddingException, InvalidKeyException, NoSuchAlgorithmException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        EncryptionData encryptedData = new EncryptionData(encryptedString);
        byte[] unencryptedBytes = decrypt(encryptedData.data, encryptedData.iv);
        return new String(unencryptedBytes, CHAR_SET);
    }

    private EncryptionData encrypt(byte[] unencryptedData, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance(AES_MODE);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        byte[] cipherIV = cipher.getIV();
        byte[] cipherData = cipher.doFinal(unencryptedData);
        return new EncryptionData(cipherIV, cipherData);
    }

    private byte[] decrypt(byte[] encryptedData, byte[] iv) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher c = Cipher.getInstance(AES_MODE);
        c.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
        return c.doFinal(encryptedData);
    }
}
