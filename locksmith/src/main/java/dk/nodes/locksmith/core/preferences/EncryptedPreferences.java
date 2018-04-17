package dk.nodes.locksmith.core.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.core.util.HashingUtils;

@SuppressLint("ApplySharedPref")
public class EncryptedPreferences {
    private SharedPreferences sharedPreferences;

    public EncryptedPreferences(Context context, String name, int mode) {
        this.sharedPreferences = context.getSharedPreferences(name, mode);
    }

    // String Encryption

    public void putString(String key, String value) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().encryptString(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public String getString(String key, String defaultValue) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, defaultValue);

        if (encryptedValue.equals(defaultValue)) {
            return defaultValue;
        }

        return Locksmith.getInstance().decryptString(encryptedValue);
    }

    // Integer Encryption

    public void putInt(String key, int value) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().encryptInt(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public int getInt(String key, int defaultValue) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().decryptInt(encryptedValue);
    }

    // Float Encryption

    public void putFloat(String key, float value) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().encryptFloat(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public float getFloat(String key, float defaultValue) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().decryptFloat(encryptedValue);
    }

    // Long Encryption

    public void putLong(String key, long value) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().encryptLong(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public long getLong(String key, long defaultValue) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().decryptLong(encryptedValue);
    }


    // Boolean Encryption

    public void putBoolean(String key, boolean value) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().encryptBoolean(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) throws LocksmithEncryptionException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().decryptBoolean(encryptedValue);
    }

    //

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
