package dk.nodes.locksmith.core.encryption;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.core.models.EncryptionData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class EncryptionManager {
    private static final String TAG = EncryptionManager.class.getSimpleName();

    private KeyStore keyStore;
    private Cipher cipher;
    private String KEY_NAME_ENCRYPTION = "LockSmithEncryptionKey";
    private Charset charset = Charset.forName("UTF-8");

    public void init(int validityDuration) {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(KEY_NAME_ENCRYPTION)) {
                KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

                keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME_ENCRYPTION, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .setUserAuthenticationValidityDurationSeconds(validityDuration)
                        .build());

                keyGenerator.generateKey();
            }

            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            Log.e(TAG, "Initiation Failed", e);
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
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME_ENCRYPTION, null);

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
            SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME_ENCRYPTION, null);

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

}
