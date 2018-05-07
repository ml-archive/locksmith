package dk.nodes.locksmith.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.encryption.EncryptionManager;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.fingerprint.FingerprintDialogBuilder;
import dk.nodes.locksmith.core.manager.FingerprintHardwareManager;
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
     * Contains our locksmith configuration
     */

    private LocksmithConfiguration locksmithConfiguration;

    /**
     * Our basic encryption manager
     */

    private EncryptionManager encryptionManager;

    /**
     * Our Fingerprint Encryption Manager (Can be null for API 23 and below)
     */

    private EncryptionManager fingerprintEncryptionManager;

    @RequiresApi(Build.VERSION_CODES.M)
    private FingerprintHardwareManager fingerprintHardwareManager;

    private Locksmith(Context context, LocksmithConfiguration locksmithConfiguration) {
        this.locksmithConfiguration = locksmithConfiguration;

        this.encryptionManager = new EncryptionManager(context, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.fingerprintEncryptionManager = new EncryptionManager(context, true);
            this.fingerprintHardwareManager = new FingerprintHardwareManager(context);
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


    /**
     * Returns whether the device can use the fingerprint api
     *
     * @return Boolean value for fingerprint availability
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public FingerprintHardwareManager.State canUseFingerprint() {
        return fingerprintHardwareManager.checkHardware();
    }

    /**
     * Returns our normal encryption manager
     *
     * @return {@link EncryptionManager}
     */

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
