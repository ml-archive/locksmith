package dk.nodes.locksmith.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.encryption.FingerprintEncryptionManager;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.core.fingerprint.FingerprintDialog;

public class Locksmith {
    @Nullable
    private static FingerprintEncryptionManager fingerprintEncryptionManager;

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintEncryptionManager = new FingerprintEncryptionManager();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String encrypt(String data) throws LocksmithEncryptionException {
        // This call requires version M so we should assume that the encryption manager will not be null
        assert fingerprintEncryptionManager != null;
        return fingerprintEncryptionManager.encrypt(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String decrypt(String data) throws LocksmithEncryptionException {
        // This call requires version M so we should assume that the encryption manager will not be null
        assert fingerprintEncryptionManager != null;
        return fingerprintEncryptionManager.decrypt(data);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static FingerprintDialog.Builder getFingerprintDialogBuilder(Context context) {
        return new FingerprintDialog.Builder(context, fingerprintEncryptionManager);
    }
}
