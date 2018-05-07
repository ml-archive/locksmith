package dk.nodes.locksmith.core.fingerprint;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Window;

import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.manager.FingerprintHardwareManager;
import dk.nodes.locksmith.core.models.FingerprintDialogEvent;
import dk.nodes.locksmith.core.models.OnFingerprintDialogEventListener;

@RequiresApi(api = Build.VERSION_CODES.M)
public abstract class FingerprintDialogBase extends Dialog {
    private static final String TAG = FingerprintDialogBase.class.getSimpleName();
    // Listeners
    protected OnFingerprintDialogEventListener onFingerprintDialogEventListener;
    // Callbacks
    private FingerprintAuthenticationCallback fingerprintAuthenticationCallback = new FingerprintAuthenticationCallback();
    // Fingerprint Related Stuff

    private FingerprintHardwareManager fingerprintHardwareManager;
    private FingerprintCryptManager cryptManager;
    private CancellationSignal cancellationSignal;

    public FingerprintDialogBase(@NonNull Context context) {
        super(context);

        this.fingerprintHardwareManager = new FingerprintHardwareManager(context);

        setContentView(getDialogLayout());
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setupWindowStyle();

        checkHardware();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void setupWindowStyle() {
        Window window = getWindow();

        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            window.setDimAmount(0.7f);
        }
    }


    private void checkHardware() {
        try {
            cryptManager = new FingerprintCryptManager();
        } catch (LocksmithException e) {
            if (onFingerprintDialogEventListener != null) {
                onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_CIPHER);
            }

            dismiss();
            return;
        }

        FingerprintHardwareManager.State state = fingerprintHardwareManager.checkHardware();

        switch (state) {
            case ERROR_INSECURE:
                if (onFingerprintDialogEventListener != null) {
                    onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_SECURE);
                }
                dismiss();
                return;
            case ERROR_HARDWARE:
                if (onFingerprintDialogEventListener != null) {
                    onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_HARDWARE);
                }
                dismiss();
                return;
            case ERROR_NO_FINGERPRINTS:
                if (onFingerprintDialogEventListener != null) {
                    onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_ENROLLMENT);
                }
                dismiss();
                return;
            case OK:
                authenticate();
        }
    }

    private void authenticate() {
        cancellationSignal = new CancellationSignal();
        FingerprintManager.CryptoObject cryptoObject = cryptManager.getCryptoObject();
        fingerprintHardwareManager.authenticate(cryptoObject, cancellationSignal, 0, fingerprintAuthenticationCallback, null);
    }

    @Override
    protected void onStop() {
        cancelSignal();
        super.onStop();
    }

    protected void cancelSignal() {
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }


    public class FingerprintAuthenticationCallback extends FingerprintManager.AuthenticationCallback {
        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            Log.d(TAG, "onAuthenticationError");
            onFingerprintHelp(errString.toString());
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            Log.d(TAG, "onAuthenticationHelp");
            onFingerprintHelp(helpString.toString());
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {
            Log.d(TAG, "onAuthenticationSucceeded");
            preDialogSuccessful();
            fingerprintAuthenticationCallback = null;
        }

        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed");
            preDialogError();
            fingerprintAuthenticationCallback = null;
        }
    }

    private void preDialogSuccessful() {
        try {
            Locksmith.getInstance().initFingerprint();
        } catch (LocksmithException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "finishDialogSuccess");

        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.SUCCESS);
        }

        onFingerprintSuccess();
    }

    // Helpers

    private void preDialogError() {
        Log.d(TAG, "finishDialogError");

        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR);
        }

        onFingerprintError();
    }

    protected void onCancelClicked() {
        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.CANCEL);
        }

        closeDialog();
    }

    protected void closeDialog() {
        Log.d(TAG, "closeDialog");
        cancelSignal();
        dismiss();
    }

    public abstract @LayoutRes
    int getDialogLayout();

    public abstract void onFingerprintSuccess();

    public abstract void onFingerprintHelp(String help);

    public abstract void onFingerprintError();
}
