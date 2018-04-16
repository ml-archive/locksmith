package dk.nodes.locksmith.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.encryption.EncryptionManager;
import dk.nodes.locksmith.core.encryption.FingerprintEncryptionManager;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;
import dk.nodes.locksmith.core.fingerprint.FingerprintDialog;

public class Locksmith {
    @Nullable
    private static FingerprintEncryptionManager fingerprintEncryptionManager;
    @Nullable
    private static EncryptionManager encryptionManager;

    private static boolean useFingerprint = false;

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintEncryptionManager = new FingerprintEncryptionManager();
            encryptionManager = new EncryptionManager();
        }
    }

    public static boolean isUseFingerprint() {
        return useFingerprint;
    }

    public static void setUseFingerprint(boolean useFingerprint) {
        Locksmith.useFingerprint = useFingerprint;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String encrypt(String data) throws LocksmithEncryptionException {
        // This call requires version M so we should assume that the encryption manager will not be null
        assert fingerprintEncryptionManager != null;
        assert encryptionManager != null;


        if (useFingerprint) {
            return fingerprintEncryptionManager.encrypt(data);
        } else {
            return encryptionManager.encrypt(data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static String decrypt(String data) throws LocksmithEncryptionException {
        // This call requires version M so we should assume that the encryption manager will not be null
        assert fingerprintEncryptionManager != null;
        assert encryptionManager != null;

        if (useFingerprint) {
            return fingerprintEncryptionManager.decrypt(data);
        } else {
            return encryptionManager.decrypt(data);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static FingerprintDialog.Builder getFingerprintDialogBuilder(Context context) {
        return new FingerprintDialog.Builder(context, fingerprintEncryptionManager);
    }
}
