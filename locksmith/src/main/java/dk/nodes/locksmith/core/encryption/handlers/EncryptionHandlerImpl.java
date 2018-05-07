package dk.nodes.locksmith.core.encryption.handlers;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.security.keystore.UserNotAuthenticatedException;
import android.support.annotation.RequiresApi;

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

import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.encryption.EncryptionHandler;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.models.EncryptionData;

@RequiresApi(api = Build.VERSION_CODES.M)
public class EncryptionHandlerImpl implements EncryptionHandler {
    private KeyStore keyStore;
    private Cipher cipher;

    /**
     * Should this instance of encryption manager use a fingerprint for authentication
     */
    private boolean useFingerprint;
    /**
     * Key name for non fingerprint key
     */
    private String KEY_NAME = "LockSmithEncryptionKey";
    /**
     * Key name for fingerprint key
     */
    private String KEY_NAME_FINGER_PRINT = "LockSmithFingerprintEncryptionKey";

    /**
     * Used for tracking initialization status
     */
    private boolean isInitialized = false;

    /**
     * Initialization constructor for our class
     *
     * @param useFingerprint Should this instance of EncryptionHandler use fingerprint authentication
     */

    public EncryptionHandlerImpl(boolean useFingerprint) {
        this.useFingerprint = useFingerprint;
    }

    /**
     * A utility method for getting the name of our Keystore
     *
     * @return Returns the appropriate key name for this instance
     */

    private String getKeyName() {
        return useFingerprint ? KEY_NAME_FINGER_PRINT : KEY_NAME;
    }

    public void init() throws LocksmithException {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(getKeyName())) {
                generateKey(getKeyName());
            }

            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);

            isInitialized = true;
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    private void generateKey(String keyName) throws NoSuchProviderException, NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        // Get our key validity duration from our Locksmith instance
        int validityDuration = Locksmith.getInstance().getLocksmithConfiguration().getKeyValidityDuration();
        // Create our key generator instance (Using the android keystore)
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        KeyGenParameterSpec.Builder builder = new KeyGenParameterSpec.Builder(keyName, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7);

        // If we enabled fingerprint we should enable the follow options for our key generator
        if (useFingerprint) {
            builder.setUserAuthenticationRequired(true);
            builder.setUserAuthenticationValidityDurationSeconds(validityDuration);
        }

        keyGenerator.init(builder.build());

        keyGenerator.generateKey();
    }

    // Byte encryption stuff

    public String encrypt(byte[] data) throws LocksmithException {
        EncryptionData encryptionData = encryptBytes(data);
        return encryptionData.encode();
    }

    public byte[] decrypt(String data) throws LocksmithException {
        EncryptionData encryptionData = new EncryptionData(data);
        return decryptBytes(encryptionData.data, encryptionData.iv);
    }

    private EncryptionData encryptBytes(byte[] data) throws LocksmithException {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(getKeyName(), null);

            cipher.init(Cipher.ENCRYPT_MODE, key);

            byte[] resultData = cipher.doFinal(data);
            byte[] resultIv = cipher.getIV();

            return new EncryptionData(resultData, resultIv);
        } catch (NullPointerException e) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated, e);
        } catch (UserNotAuthenticatedException e) {
            throw new LocksmithException(LocksmithException.Type.Unauthenticated, e);
        } catch (InvalidKeyException | BadPaddingException e) {
            throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithException(LocksmithException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
            }
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Generic, e);
        }
    }

    private byte[] decryptBytes(byte[] data, byte[] iv) throws LocksmithException {
        try {
            keyStore.load(null);
            SecretKey key = (SecretKey) keyStore.getKey(getKeyName(), null);

            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
            return cipher.doFinal(data);
        } catch (NullPointerException e) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated, e);
        } catch (UserNotAuthenticatedException e) {
            throw new LocksmithException(LocksmithException.Type.Unauthenticated, e);
        } catch (InvalidKeyException | BadPaddingException | InvalidAlgorithmParameterException e) {
            throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithException(LocksmithException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
            }
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Generic, e);
        }
    }

}
