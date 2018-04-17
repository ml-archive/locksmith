package dk.nodes.locksmith.core.encryption.handlers;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import dk.nodes.locksmith.core.encryption.EncryptionHandler;
import dk.nodes.locksmith.core.exceptions.LocksmithCreationException;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.core.models.EncryptionData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class EncryptionHandlerImpl implements EncryptionHandler {
    private static final String TAG = EncryptionHandlerImpl.class.getSimpleName();

    private KeyStore keyStore;
    private Cipher cipher;

    private String KEY_NAME = "LockSmithEncryptionKey";

    public void init() throws LocksmithCreationException {
        Log.d(TAG, "Init");

        if (cipher != null) {
            return;
        }

        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);


            if (!keyStore.containsAlias(KEY_NAME)) {
                generateKey(KEY_NAME);
            }

            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            throw new LocksmithCreationException(e);
        }
    }

    private void generateKey(String keyName) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

        keyGenerator.init(builder.build());

        keyGenerator.generateKey();
    }

    // Byte encryption stuff

    public String encrypt(byte[] data) throws LocksmithEncryptionException {
        EncryptionData encryptionData = encryptBytes(data);
        return encryptionData.encode();
    }

    public byte[] decrypt(String data) throws LocksmithEncryptionException {
        EncryptionData encryptionData = new EncryptionData(data);
        return decryptBytes(encryptionData.data, encryptionData.iv);
    }

    private EncryptionData encryptBytes(byte[] data) throws LocksmithEncryptionException {
        try {
            init();

            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] resultData = cipher.doFinal(data);
            byte[] resultIv = cipher.getIV();

            return new EncryptionData(resultData, resultIv);
        } catch (NullPointerException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Uninitiated, e);
        } catch (UserNotAuthenticatedException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
        } catch (InvalidKeyException | BadPaddingException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.EncryptionError, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.EncryptionError, e);
            }
        } catch (Exception e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Generic, e);
        }
    }

    private byte[] decryptBytes(byte[] data, byte[] iv) throws LocksmithEncryptionException {
        try {
            init();

            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME, null);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (NullPointerException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Uninitiated, e);
        } catch (UserNotAuthenticatedException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
        } catch (InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.EncryptionError, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.EncryptionError, e);
            }
        } catch (Exception e) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Generic, e);
        }
    }
}
