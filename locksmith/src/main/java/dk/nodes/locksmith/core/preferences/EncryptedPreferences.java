package dk.nodes.locksmith.core.preferences;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.util.HashingUtils;

@SuppressLint("ApplySharedPref")
public class EncryptedPreferences {
    private SharedPreferences sharedPreferences;

    EncryptedPreferences(Context context, String name, int mode) {
        this.sharedPreferences = context.getSharedPreferences(name, mode);
    }

    // String Encryption

    public void putString(String key, String value) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().getEncryptionManager().encryptString(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public String getString(String key, String defaultValue) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, defaultValue);

        if (encryptedValue.equals(defaultValue)) {
            return defaultValue;
        }

        return Locksmith.getInstance().getEncryptionManager().decryptString(encryptedValue);
    }

    // Integer Encryption

    public void putInt(String key, int value) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().getEncryptionManager().encryptInt(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public int getInt(String key, int defaultValue) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().getEncryptionManager().decryptInt(encryptedValue);
    }

    // Float Encryption

    public void putFloat(String key, float value) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().getEncryptionManager().encryptFloat(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public float getFloat(String key, float defaultValue) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().getEncryptionManager().decryptFloat(encryptedValue);
    }

    // Long Encryption

    public void putLong(String key, long value) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().getEncryptionManager().encryptLong(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public long getLong(String key, long defaultValue) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().getEncryptionManager().decryptLong(encryptedValue);
    }


    // Boolean Encryption

    public void putBoolean(String key, boolean value) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedData = Locksmith.getInstance().getEncryptionManager().encryptBoolean(value);
        sharedPreferences.edit().putString(hashedKey, encryptedData).commit();
    }

    public boolean getBoolean(String key, boolean defaultValue) throws LocksmithException {
        String hashedKey = HashingUtils.sha256(key);
        String encryptedValue = sharedPreferences.getString(hashedKey, null);

        if (encryptedValue == null) {
            return defaultValue;
        }

        return Locksmith.getInstance().getEncryptionManager().decryptBoolean(encryptedValue);
    }

    //

    public void remove(String key) {
        String hashedKey = HashingUtils.sha256(key);
        sharedPreferences.edit().remove(hashedKey).commit();
    }

    public void clear() {
        sharedPreferences.edit().clear().commit();
    }
}
