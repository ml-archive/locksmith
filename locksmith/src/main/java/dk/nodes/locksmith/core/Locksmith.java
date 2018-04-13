package dk.nodes.locksmith.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.encryption.EncryptionManager;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;

public class Locksmith {
    @Nullable
    public static EncryptionManager encryptionManager;

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptionManager = new EncryptionManager();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String encrypt(String data) throws LocksmithEncryptionException {
        // This call requires version M so we should assume that the encryption manager will not be null
        assert encryptionManager != null;
        return encryptionManager.encryptString(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String decrypt(String data) throws LocksmithEncryptionException {
        // This call requires version M so we should assume that the encryption manager will not be null
        assert encryptionManager != null;
        return encryptionManager.decryptString(data);
    }
}
