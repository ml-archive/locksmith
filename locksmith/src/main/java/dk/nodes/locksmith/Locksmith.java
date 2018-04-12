package dk.nodes.locksmith;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import dk.nodes.locksmith.fingerprint.FingerprintCryptManager;

public class Locksmith {
    private static Locksmith instance;
    @NonNull
    public static FingerprintCryptManager cryptManager;

    /**
     * Static stuff
     */

    public static void init(Context context) {
        instance = new Locksmith(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cryptManager = new FingerprintCryptManager();
        }
    }

    public static Locksmith getInstance() {
        return instance;
    }

    // Normal Class Stuff

    private Locksmith(Context context) {

    }
}
