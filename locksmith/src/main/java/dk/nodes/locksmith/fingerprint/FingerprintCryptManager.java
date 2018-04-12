package dk.nodes.locksmith.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
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
import dk.nodes.locksmith.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.models.EncryptionData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintCryptManager {
    private static final String TAG = FingerprintCryptManager.class.getSimpleName();

    private KeyStore keyStore;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private String KEY_NAME = "LockSmithFingerprintKey";
    private Charset charset = Charset.forName("UTF-8");
    private int validityDuration = 60;

    public void generateCypher(int validityDuration) throws CipherCreationException {
        this.validityDuration = validityDuration;

        try {
            generateKey(false);
            getCipher();
            generateCipher();
            generateCypherObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CipherCreationException();
        }
    }

    public void generateKey() throws LocksmithEncryptionException {
        try {
            generateKey(true);
        } catch (Exception e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Generic, e);
        }
    }

    public String encryptString(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = data.getBytes(charset);
        EncryptionData encryptionData = encrypt(decryptedData);
        return encryptionData.encode();
    }

    public String decryptString(String data) throws LocksmithEncryptionException {
        EncryptionData encryptionData = new EncryptionData(data);
        byte[] decryptedData = decrypt(encryptionData.data, encryptionData.iv);
        return new String(decryptedData, charset);
    }

    private EncryptionData encrypt(byte[] data) throws LocksmithEncryptionException {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] resultData = cipher.doFinal(data);
            byte[] resultIv = cipher.getIV();

            return new EncryptionData(resultData, resultIv);
        } catch (NullPointerException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.UninitiatedCipher, e);
        } catch (UserNotAuthenticatedException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
        } catch (InvalidKeyException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.InvalidKey, e);
        } catch (BadPaddingException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.BadPadding, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.IllegalBlockSize, e);
            }
        } catch (Exception e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Generic, e);
        }
    }

    private byte[] decrypt(byte[] data, byte[] iv) throws LocksmithEncryptionException {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (NullPointerException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.UninitiatedCipher, e);
        } catch (UserNotAuthenticatedException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
        } catch (InvalidKeyException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.InvalidKey, e);
        } catch (BadPaddingException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.BadPadding, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.IllegalBlockSize, e);
            }
        } catch (InvalidAlgorithmParameterException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.InvalidAlgorithm, e);
        } catch (Exception e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Generic, e);
        }
    }


    private void generateKey(boolean withTimeValidity) throws InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        if (!keyStore.containsAlias(KEY_NAME)) {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

            keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationValidityDurationSeconds(60)
                    .build());

            keyGenerator.generateKey();
        }
    }

    private void getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
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
