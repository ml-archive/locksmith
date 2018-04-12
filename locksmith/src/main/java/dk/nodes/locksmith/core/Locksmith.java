package dk.nodes.locksmith.core;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;

import dk.nodes.locksmith.core.encryption.EncryptionManager;

public class Locksmith {
    @Nullable
    public static EncryptionManager encryptionManager;

    public static void init(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            encryptionManager = new EncryptionManager();
        }
    }
}
