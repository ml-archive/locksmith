package dk.nodes.locksmith.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import dk.nodes.locksmith.exceptions.CipherCreationException;
import dk.nodes.locksmith.exceptions.InvalidDataException;
import dk.nodes.locksmith.models.EncryptionData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintCryptManager {
    private static final String TAG = FingerprintCryptManager.class.getSimpleName();

    private String KEY_NAME = "LockSmithFingerprintKey";
    private KeyStore keyStore;
    private KeyGenerator keyGenerator;
    private SecretKey key;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;
    private Charset charset = Charset.forName("UTF-8");
    private final int GCM_TAG_LENGTH = 128;
    private boolean isStarted = false;

    public FingerprintCryptManager() {
        try {
            start();
        } catch (CipherCreationException e) {
            e.printStackTrace();
        }
    }

    // Exposed Functions

    public void start() throws CipherCreationException {
        if (isStarted) {
            return;
        }

        try {
            getKeyStore();
            getKeyGenerator();
            generateKey();
            getCipher();
            generateCipher();
            generateCypherObject();

            isStarted = true;
        } catch (Exception e) {
            throw new CipherCreationException();
        }
    }

    public String encryptString(String data) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        byte[] decryptedData = data.getBytes(charset);
        EncryptionData encryptionData = encrypt(decryptedData);
        Log.d(TAG, "Encryption: " + encryptionData.toString());
        return encryptionData.encode();
    }

    public String decryptString(String data) throws InvalidDataException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
        EncryptionData encryptionData = new EncryptionData(data);
        byte[] decryptedData = decrypt(encryptionData.data, encryptionData.iv);
        return new String(decryptedData, charset);
    }

    private EncryptionData encrypt(byte[] data) throws
            InvalidKeyException,
            BadPaddingException,
            IllegalBlockSizeException {

        cipher.init(Cipher.ENCRYPT_MODE, key);

        byte[] resultData = cipher.doFinal(data);
        byte[] resultIv = cipher.getIV();

        return new EncryptionData(resultData, resultIv);
    }

    private byte[] decrypt(byte[] data, byte[] iv) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, InvalidAlgorithmParameterException {
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        return cipher.doFinal(data);
    }

    private void getKeyStore() throws KeyStoreException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
    }

    private void getKeyGenerator() throws NoSuchProviderException, NoSuchAlgorithmException {
        keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");
    }

    private void generateKey() throws InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, IOException {
        keyStore.load(null);

        keyGenerator.init(new
                KeyGenParameterSpec.Builder(KEY_NAME,
                KeyProperties.PURPOSE_ENCRYPT |
                        KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setUserAuthenticationValidityDurationSeconds(600)
                .setEncryptionPaddings(
                        KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build());

        key = keyGenerator.generateKey();
    }

    private void getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        // Generate our cypher
        cipher = Cipher.getInstance(
                KeyProperties.KEY_ALGORITHM_AES + "/"
                        + KeyProperties.BLOCK_MODE_CBC + "/"
                        + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    private void generateCipher() throws InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        // Load our key from our keystore
        keyStore.load(null);
        SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);
        // Start our cypher
        cipher.init(Cipher.ENCRYPT_MODE, key);
    }

    private void generateCypherObject() {
        cryptoObject = new FingerprintManager.CryptoObject(cipher);
    }

    public FingerprintManager.CryptoObject getCryptoObject() {
        return cryptoObject;
    }
}
