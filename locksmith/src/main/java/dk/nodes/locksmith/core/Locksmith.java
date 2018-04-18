package dk.nodes.locksmith.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.encryption.EncryptionManager;
import dk.nodes.locksmith.core.exceptions.LocksmithCreationException;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.core.fingerprint.FingerprintDialogBase;
import dk.nodes.locksmith.core.fingerprint.FingerprintDialogBuilder;

@SuppressLint("StaticFieldLeak")
public class Locksmith {
    private static Locksmith locksmith;

    public static Locksmith getInstance() {
        return locksmith;
    }

    private boolean useFingerprint = false;
    private int keyValidityDuration = 120;
    private Context context;

    private EncryptionManager encryptionManager = new EncryptionManager();

    public void init() throws LocksmithCreationException {
        encryptionManager.init(context);
    }

    // String Encrypt/Decrypt

    public String encryptString(String data) throws LocksmithEncryptionException {
        return encryptionManager.encryptString(data);
    }

    public String decryptString(String data) throws LocksmithEncryptionException {
        return encryptionManager.decryptString(data);
    }

    // Int Encrypt/Decrypt

    public String encryptInt(int data) throws LocksmithEncryptionException {
        return encryptionManager.encryptInt(data);
    }

    public int decryptInt(String data) throws LocksmithEncryptionException {
        return encryptionManager.decryptInt(data);
    }

    // Boolean Encrypt/Decrypt

    public String encryptBoolean(boolean data) throws LocksmithEncryptionException {
        return encryptionManager.encryptBoolean(data);
    }

    public boolean decryptBoolean(String data) throws LocksmithEncryptionException {
        return encryptionManager.decryptBoolean(data);
    }

    // Float Encrypt/Decrypt

    public String encryptFloat(float data) throws LocksmithEncryptionException {
        return encryptionManager.encryptFloat(data);
    }

    public float decryptFloat(String data) throws LocksmithEncryptionException {
        return encryptionManager.decryptFloat(data);
    }

    // Long Encrypt/Decrypt

    public String encryptLong(long data) throws LocksmithEncryptionException {
        return encryptionManager.encryptLong(data);
    }

    public long decryptLong(String data) throws LocksmithEncryptionException {
        return encryptionManager.decryptLong(data);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintDialogBuilder getFingerprintDialogBuilder(Context context) {
        return new FingerprintDialogBuilder(context);
    }

    // Getters

    public boolean isUseFingerprint() {
        return useFingerprint;
    }

    public int getKeyValidityDuration() {
        return keyValidityDuration;
    }

    //Builder

    public static class Builder {
        public Builder(Context context) {
            locksmith = new Locksmith();
            locksmith.context = context;
        }

        @RequiresApi(Build.VERSION_CODES.M)
        public Builder setKeyValidityDuration(int seconds) {
            locksmith.keyValidityDuration = seconds;
            return this;
        }

        @RequiresApi(Build.VERSION_CODES.M)
        public Builder setUseFingerprint(boolean useFingerprint) {
            locksmith.useFingerprint = useFingerprint;
            return this;
        }

        public Locksmith build() {
            return locksmith;
        }
    }
}
