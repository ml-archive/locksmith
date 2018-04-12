package dk.nodes.locksmith;

import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import dk.nodes.locksmith.encryption.EncryptionManager;

public class Locksmith {
    private static Locksmith instance;
    @NonNull
    public static EncryptionManager encryptionManager;

    /**
     * Static stuff
     */

    public static void init(Context context) {
        instance = new Locksmith(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptionManager = new EncryptionManager();
        }
    }

    public static Locksmith getInstance() {
        return instance;
    }

    // Normal Class Stuff

    private Locksmith(Context context) {

    }
}
