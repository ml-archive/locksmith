package dk.nodes.locksmith.core.encryption.providers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import dk.nodes.locksmith.core.encryption.compat.RsaEncryptionHelper;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.util.HashingUtils;

public class CompatAndroidKeyProviderImpl implements KeyProvider {
    //Lengths
    private final int AES_BIT_LENGTH = 256;
    //AES Key
    private static final String AES_MODE = "AES/GCM/NoPadding";
    //Encryption
    private static final String SP_AES_KEY = "SP_AES_KEY";
    //General Stuff
    private Context context;
    private SharedPreferences sharedPreferences;
    private RsaEncryptionHelper rsaEncryptionHelper;
    private SecretKeySpec aesKey;

    public CompatAndroidKeyProviderImpl(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void init() throws LocksmithException {
        this.rsaEncryptionHelper = new RsaEncryptionHelper(context);
        loadAesKeys();
    }

    private void loadAesKeys() throws LocksmithException {
        try {
            // In order to load our AES key we need to first hash our value
            String hashedKey = HashingUtils.sha256(SP_AES_KEY);
            // Then load our encrypted key from our shared preferences
            String encodedKey = sharedPreferences.getString(hashedKey, null);

            byte[] key;

            if (encodedKey == null) {
                // Generate our key if we don't have it
                KeyGenerator keyGen = KeyGenerator.getInstance("AES");
                keyGen.init(AES_BIT_LENGTH);
                SecretKey sKey = keyGen.generateKey();
                key = sKey.getEncoded();

                // Once we have a key we need to encrypt it
                byte[] encryptedKey = rsaEncryptionHelper.encrypt(key);
                // Then encode it
                encodedKey = Base64.encodeToString(encryptedKey, Base64.NO_WRAP);
                // Then save it for later use
                sharedPreferences.edit().putString(hashedKey, encodedKey).apply();

                // Set our key and return we're done
                aesKey = new SecretKeySpec(key, "AES");
                return;
            }

            // If we do have a key just decode it
            key = Base64.decode(encodedKey, Base64.NO_WRAP);
            // Then decrypt it
            key = rsaEncryptionHelper.decrypt(key);

            //And then set it
            aesKey = new SecretKeySpec(key, "AES");
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

    @Override
    public Key getKey() throws LocksmithException {

        if (aesKey == null) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated);
        }

        return aesKey;
    }

    @Override
    public Cipher getCipher() throws LocksmithException {
        try {
            return Cipher.getInstance(AES_MODE);
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }
}
