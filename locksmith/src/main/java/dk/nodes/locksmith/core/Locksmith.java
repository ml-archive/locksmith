package dk.nodes.locksmith.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.encryption.EncryptionManager;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.fingerprint.FingerprintDialogBuilder;
import dk.nodes.locksmith.core.models.LocksmithConfiguration;

public class Locksmith {
    public static Locksmith instance;

    public static void init(Context context, LocksmithConfiguration locksmithConfiguration) {
        instance = new Locksmith(context, locksmithConfiguration);
    }

    public static Locksmith getInstance() {
        return instance;
    }

    /**
     * Duration for how long our key will be valid for after it is validated by fingerprint (Measured in seconds)
     */
    private LocksmithConfiguration locksmithConfiguration;

    private EncryptionManager encryptionManager;
    private EncryptionManager fingerprintEncryptionManager;

    private Locksmith(Context context, LocksmithConfiguration locksmithConfiguration) {
        this.locksmithConfiguration = locksmithConfiguration;

        this.encryptionManager = new EncryptionManager(context, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.fingerprintEncryptionManager = new EncryptionManager(context, true);
        }
    }

    /**
     * Initialization for our fingerprint encryption manager
     *
     * @throws LocksmithException
     */
    @RequiresApi(Build.VERSION_CODES.M)
    public void initFingerprint() throws LocksmithException {
        fingerprintEncryptionManager.init();
    }

    /**
     * Initialization for our basic encryption
     *
     * @throws LocksmithException
     */

    public void init() throws LocksmithException {
        encryptionManager.init();
    }

    @NonNull
    public EncryptionManager getEncryptionManager() {
        return encryptionManager;
    }

    /**
     * Returns an instance of our fingerprint EncryptionManager
     *
     * @return {@link EncryptionManager} instance
     */

    @NonNull
    @RequiresApi(Build.VERSION_CODES.M)
    public EncryptionManager getFingerprintEncryptionManager() {
        return fingerprintEncryptionManager;
    }

    /**
     * Returns a builder for a simple Fingerprint Dialog
     *
     * @param context Context must be provided to create the dialog
     * @return {@link FingerprintDialogBuilder} instance
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintDialogBuilder getFingerprintDialogBuilder(Context context) {
        return new FingerprintDialogBuilder(context);
    }

    /**
     * Returns the current configuration
     *
     * @return {@link LocksmithConfiguration}
     */
    public LocksmithConfiguration getLocksmithConfiguration() {
        return locksmithConfiguration;
    }
}
