package dk.nodes.locksmith.core.manager;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;

@RequiresApi(Build.VERSION_CODES.M)
public class FingerprintHardwareManager {
    public enum State {
        /**
         * Fingerprint hardware is not present
         */
        ERROR_HARDWARE,
        /**
         * Device has lock screen disabled
         */
        ERROR_INSECURE,
        /**
         * Device has no fingerprints enrolled
         */
        ERROR_NO_FINGERPRINTS,
        /**
         * Device is able to use the Fingerprint API
         */
        OK
    }

    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;

    public FingerprintHardwareManager(Context context) {
        this.keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        this.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
    }

    public State checkHardware() {
        if (!keyguardManager.isDeviceSecure()) {
            return State.ERROR_INSECURE;
        }

        if (!fingerprintManager.isHardwareDetected()) {
            return State.ERROR_HARDWARE;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            return State.ERROR_NO_FINGERPRINTS;
        }

        return State.OK;
    }

    public void authenticate(@Nullable FingerprintManager.CryptoObject crypto, @Nullable CancellationSignal cancel, int flags, @NonNull FingerprintManager.AuthenticationCallback callback, @Nullable Handler handler) {
        this.fingerprintManager.authenticate(crypto, cancel, flags, callback, handler);
    }
}
