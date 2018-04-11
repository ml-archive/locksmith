package dk.nodes.locksmith;

import android.content.Context;
import android.os.Build;

import dk.nodes.locksmith.fingerprint.FingerprintCryptManager;

public class Locksmith {
    private static Locksmith instance;

    /**
     * Static stuff
     */

    public static void init(Context context) {
        instance = new Locksmith(context);
    }

    public static Locksmith getInstance() {
        return instance;
    }

    // Normal Class Stuff

    private FingerprintCryptManager fingerprintCryptManager;

    private Locksmith(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            fingerprintCryptManager = new FingerprintCryptManager();
        }
    }

    public FingerprintCryptManager getFingerprintCryptManager() {
        return fingerprintCryptManager;
    }

    public void encrypt(byte[] data) {

    }

    public void decrypt(byte[] data) {

    }
}
