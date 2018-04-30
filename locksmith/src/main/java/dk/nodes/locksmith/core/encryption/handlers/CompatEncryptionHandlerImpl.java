package dk.nodes.locksmith.core.encryption.handlers;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Base64;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStoreException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import dk.nodes.locksmith.core.encryption.EncryptionHandler;
import dk.nodes.locksmith.core.encryption.compat.RsaEncryptionHelper;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.models.EncryptionData;
import dk.nodes.locksmith.core.util.HashingUtils;

public class CompatEncryptionHandlerImpl implements EncryptionHandler {
    //Lengths
    private final int AES_BIT_LENGTH = 256;
    private final int GCM_TAG_LENGTH = 128;
    //AES Key
    private static final String AES_MODE = "AES/GCM/NoPadding";
    //Encryption
    private static final String CHAR_SET = "UTF-8";
    private static final String SP_AES_KEY = "SP_AES_KEY";
    //General Stuff
    private Context context;
    private SharedPreferences sharedPreferences;
    private RsaEncryptionHelper rsaEncryptionHelper;
    private SecretKeySpec aesKey;
    /**
     * Used for tracking initialization status
     */
    private boolean isInitialized = false;

    public CompatEncryptionHandlerImpl(Context context) {
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public void init() throws LocksmithException {
        this.rsaEncryptionHelper = new RsaEncryptionHelper(context);
        loadAesKeys();
        isInitialized = true;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    //Main Decrypting Methods

    @Override
    public String encrypt(byte[] data) throws LocksmithException {
        byte[] iv = getIV();
        EncryptionData encryptedData = encrypt(data, iv);
        return encryptedData.encode();
    }

    @Override
    public byte[] decrypt(String data) throws LocksmithException {
        EncryptionData encryptedData = new EncryptionData(data);
        return decrypt(encryptedData.data, encryptedData.iv);
    }

    private byte[] getIV() {
        byte[] iv;
        iv = new byte[16];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(iv);
        return iv;
    }

    //AES Key Stuff

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

    private EncryptionData encrypt(byte[] data, byte[] iv) throws LocksmithException {
        try {
            Cipher cipher = Cipher.getInstance(AES_MODE);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            EncryptionData result = new EncryptionData();
            result.iv = cipher.getIV();
            result.data = cipher.doFinal(data);

            return result;
        } catch (NullPointerException | InvalidKeyException | BadPaddingException e) {
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


    private byte[] decrypt(byte[] data, byte[] iv) throws LocksmithException {
        try {
            Cipher c = Cipher.getInstance(AES_MODE, "BC");
            c.init(Cipher.DECRYPT_MODE, aesKey, new GCMParameterSpec(GCM_TAG_LENGTH, iv));
            return c.doFinal(data);
        } catch (NullPointerException e) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated, e);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException | BadPaddingException e) {
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
