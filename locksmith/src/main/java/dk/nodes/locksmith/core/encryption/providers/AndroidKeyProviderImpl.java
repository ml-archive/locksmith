package dk.nodes.locksmith.core.encryption.providers;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.exceptions.LocksmithException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class AndroidKeyProviderImpl implements KeyProvider {
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
     * Initialization constructor for our class
     *
     * @param useFingerprint Should this instance of EncryptionHandler use fingerprint authentication
     */

    public AndroidKeyProviderImpl(boolean useFingerprint) {
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

    @Override
    public void init() throws LocksmithException {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);

            if (!keyStore.containsAlias(getKeyName())) {
                generateKey(getKeyName());
            }

        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
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

    @Override
    public Key getKey() throws LocksmithException {
        try {
            KeyStore keyStore = KeyStore.getInstance("AndroidKeyStore");
            keyStore.load(null);
            return keyStore.getKey(getKeyName(), null);
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

    @Override
    public Cipher getCipher() throws LocksmithException {
        try {
            return Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

}
