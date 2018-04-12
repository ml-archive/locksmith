package dk.nodes.locksmith.core.fingerprint;

import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.support.annotation.ColorRes;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.nodes.locksmith.R;
import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.exceptions.CipherCreationException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintDialog extends Dialog {
    private static final String TAG = FingerprintDialog.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class Builder {
        FingerprintDialog fingerprintDialog;
        Context context;

        public Builder(Context context) {
            this.context = context;
            this.fingerprintDialog = new FingerprintDialog(context);
        }

        public Builder setEventListener(OnFingerprintDialogEventListener onFingerprintDialogEventListener) {
            fingerprintDialog.onFingerprintDialogEventListener = onFingerprintDialogEventListener;
            return this;
        }

        public Builder setCancelText(String text) {
            fingerprintDialog.cancelButtonText = text;
            return this;
        }

        public Builder setSuccessMessage(String message) {
            fingerprintDialog.successMessageText = message;
            return this;
        }

        public Builder setErrorMessage(String message) {
            fingerprintDialog.errorMessageText = message;
            return this;
        }

        public Builder setKeyValidityDuration(int validityDuration) {
            fingerprintDialog.validityDuration = validityDuration;
            return this;
        }

        public Builder setTitle(String text) {
            fingerprintDialog.titleText = text;
            return this;
        }

        public Builder setSubtitle(String text) {
            fingerprintDialog.subtitleText = text;
            return this;
        }

        public Builder setDescription(String text) {
            fingerprintDialog.descriptionText = text;
            return this;
        }

        public FingerprintDialog build() throws CipherCreationException {
            fingerprintDialog.keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            fingerprintDialog.fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            fingerprintDialog.cryptManager = new FingerprintCryptManager();

            context = null;

            return fingerprintDialog;
        }
    }

    public enum FingerprintDialogEvent {
        /**
         * Is called when a user cancels the dialog
         */
        CANCEL,
        /**
         * Is called when a fingerprint is succesfully accepted
         */
        SUCCESS,
        /**
         * Is called when a fingerprint is read but not accepted
         */
        ERROR,
        /**
         * Is called when the lock screen is not secured by a code/fingerprint
         */
        ERROR_SECURE,
        /**
         * Is sent when there is no hardware detected¬
         */
        ERROR_HARDWARE,
        /**
         * Is called when no finger prints are enrolled
         */
        ERROR_ENROLLMENT
    }

    public interface OnFingerprintDialogEventListener {
        void onFingerprintEvent(@NonNull FingerprintDialogEvent event);
    }

    // Listeners
    private OnFingerprintDialogEventListener onFingerprintDialogEventListener;

    // Views
    private LinearLayout mainContainer;

    private TextView tvTitle;
    private TextView tvSubtitle;
    private TextView tvDescription;
    private TextView tvMessage;

    private ImageView ivFingerprint;
    private Button btnCancel;

    // Callbacks
    private FingerprintAuthenticationCallback fingerprintAuthenticationCallback = new FingerprintAuthenticationCallback();

    // Fingerprint Related Stuff
    private KeyguardManager keyguardManager;
    private FingerprintManager fingerprintManager;
    private FingerprintCryptManager cryptManager;

    private CancellationSignal cancellationSignal;

    // Handler
    private Handler handler = new Handler();

    // Values
    private int validityDuration = 60;
    private String titleText;
    private String subtitleText;
    private String descriptionText;
    private String cancelButtonText;
    private String successMessageText;
    private String errorMessageText;


    private FingerprintDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_fingerprint);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        setupWindowStyle();

        bindViews();

        setupViews();

        checkHardware();

        startShowAnimation();
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

    private void bindViews() {
        if (getWindow() == null) {
            dismiss();
            return;
        }

        mainContainer = getWindow().findViewById(R.id.dialogFragmentMainContainer);

        tvTitle = getWindow().findViewById(R.id.dialogFragmentTvTitle);
        tvSubtitle = getWindow().findViewById(R.id.dialogFragmentTvSubtitle);
        tvDescription = getWindow().findViewById(R.id.dialogFragmentTvDescription);
        tvMessage = getWindow().findViewById(R.id.dialogFragmentTvMessage);

        ivFingerprint = getWindow().findViewById(R.id.dialogFragmentIvFingerprint);

        btnCancel = getWindow().findViewById(R.id.dialogFragmentBtnCancel);
    }

    private void setupViews() {
        // If our cancel button is setup then we should show it and add the values

        if (cancelButtonText != null) {
            btnCancel.setText(cancelButtonText);
        }

        if (titleText != null) {
            tvTitle.setText(titleText);
        }

        if (subtitleText != null) {
            tvSubtitle.setText(subtitleText);
        }

        if (descriptionText != null) {
            tvDescription.setText(descriptionText);
        }

        btnCancel.setOnClickListener(this::onCancelClicked);
    }

    // Fingerprint Methods

    private void checkHardware() {
        if (!keyguardManager.isDeviceSecure()) {
            if (onFingerprintDialogEventListener != null) {
                onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_SECURE);
            }

            dismiss();
            return;
        }

        if (!fingerprintManager.isHardwareDetected()) {
            if (onFingerprintDialogEventListener != null) {
                onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_HARDWARE);
            }

            dismiss();
            return;
        }

        if (!fingerprintManager.hasEnrolledFingerprints()) {
            if (onFingerprintDialogEventListener != null) {
                onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR_ENROLLMENT);
            }

            dismiss();
            return;
        }

        authenticate();
    }

    private void authenticate() {
        cancellationSignal = new CancellationSignal();
        FingerprintManager.CryptoObject cryptoObject = cryptManager.getCryptoObject();
        fingerprintManager.authenticate(cryptoObject, cancellationSignal, 0, fingerprintAuthenticationCallback, null);
    }

    // Event
    private void onFingerprintSuccess() {
        setFingerprintBackgroundTint(R.color.fingerprint_success);

        if (successMessageText != null) {
            setTvMessageWithStyle(successMessageText, R.style.FingerprintDialogSuccess);
            btnCancel.setEnabled(false);
            handler.postDelayed(this::finishDialogSuccess, 1000);
        } else {
            finishDialogSuccess();
        }
    }

    private void onFingerprintError() {
        setFingerprintBackgroundTint(R.color.fingerprint_error);

        if (errorMessageText != null) {
            setTvMessageWithStyle(errorMessageText, R.style.FingerprintDialogError);
            btnCancel.setEnabled(false);
            handler.postDelayed(this::finishDialogError, 1000);
        } else {
            finishDialogError();
        }
    }

    private void onFingerprintHelp(String helpString) {
        Log.d(TAG, "onFingerprintHelp: " + helpString);

        setFingerprintBackgroundTint(R.color.fingerprint_help);

        setTvMessageWithStyle(helpString, R.style.FingerprintDialogWarn);
    }

    // Helpers

    private void setTvMessageWithStyle(String message, @StyleRes int styleRes) {
        tvMessage.setVisibility(View.VISIBLE);
        tvMessage.setText(message);
        tvMessage.setTextAppearance(styleRes);
    }

    private void setFingerprintBackgroundTint(@ColorRes int colorRes) {
        Log.d(TAG, "setFingerprintBackgroundTint: " + colorRes);

        Context context = getContext();

        int color = ContextCompat.getColor(context, colorRes);
        ColorStateList stateList = ColorStateList.valueOf(color);
        ivFingerprint.setBackgroundTintList(stateList);
    }

    // Dialog Finishers

    private void finishDialogSuccess() {
        Log.d(TAG, "finishDialogSuccess");

        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.SUCCESS);
        }

        closeDialog();
    }

    private void finishDialogError() {
        Log.d(TAG, "finishDialogError");

        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.ERROR);
        }

        closeDialog();
    }

    private void onCancelClicked(View view) {
        Log.d(TAG, "onCancelClicked: " + view);

        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.CANCEL);
        }

        closeDialog();
    }

    private void closeDialog() {
        startCloseAnimation();
        cancelSignal();
    }

    // Event overrides

    @Override
    protected void onStop() {
        cancelSignal();
        super.onStop();
    }


    // Cancel Signal

    private void cancelSignal() {
        if (cancellationSignal != null && !cancellationSignal.isCanceled()) {
            cancellationSignal.cancel();
        }
    }

    // Animation Functions

    private void startShowAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_up);
        mainContainer.startAnimation(animation);
    }

    private void startCloseAnimation() {
        Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.slide_down);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                onCloseAnimationFinished();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mainContainer.startAnimation(animation);
    }

    private void onCloseAnimationFinished() {
        dismiss();
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

            Locksmith.encryptionManager.init(validityDuration);

            onFingerprintSuccess();

            fingerprintAuthenticationCallback = null;
        }

        @Override
        public void onAuthenticationFailed() {
            Log.d(TAG, "onAuthenticationFailed");
            onFingerprintError();
            fingerprintAuthenticationCallback = null;
        }
    }
}
