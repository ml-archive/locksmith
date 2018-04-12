package dk.nodes.locksmith.core.fingerprint;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import dk.nodes.locksmith.R;
import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.exceptions.CipherCreationException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintDialog extends DialogFragment {
    private static final String TAG = FingerprintDialog.class.getSimpleName();

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static class Builder {
        int validityTime = 60;
        FingerprintDialog fingerprintDialog;
        Context context;

        public Builder(Context context) {
            this.context = context;
            this.fingerprintDialog = new FingerprintDialog();
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

        public Builder setKeyValidityDuration(int time) {
            this.validityTime = time;
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

    // Bottom Sheet
    private BottomSheetBehavior bottomSheetBehavior;

    // Callbacks
    private FingerprintAuthenticationCallback fingerprintAuthenticationCallback = new FingerprintAuthenticationCallback();
    private BottomSheetCallback bottomSheetCallback = new BottomSheetCallback();

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


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.dialog_fingerprint, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated");

        super.onViewCreated(view, savedInstanceState);

        setupWindowStyle();

        bindViews(view);

        setupViews();

        checkHardware();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme_Material_Light_Dialog);

    }

    private void setupWindowStyle() {
        Dialog dialog = getDialog();

        if (dialog != null) {
            dialog.setCancelable(true);

            Window window = dialog.getWindow();

            if (window != null) {
                window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                window.setDimAmount(0.7f);
            }
        }
    }

    private void bindViews(View view) {
        mainContainer = view.findViewById(R.id.dialogFragmentMainContainer);

        tvTitle = view.findViewById(R.id.dialogFragmentTvTitle);
        tvSubtitle = view.findViewById(R.id.dialogFragmentTvSubtitle);
        tvDescription = view.findViewById(R.id.dialogFragmentTvDescription);
        tvMessage = view.findViewById(R.id.dialogFragmentTvMessage);

        ivFingerprint = view.findViewById(R.id.dialogFragmentIvFingerprint);

        btnCancel = view.findViewById(R.id.dialogFragmentBtnCancel);
    }

    private void setupViews() {
        // Setup Bottom Sheet
        bottomSheetBehavior = BottomSheetBehavior.from(mainContainer);

        bottomSheetBehavior.setPeekHeight(0);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        bottomSheetBehavior.setBottomSheetCallback(bottomSheetCallback);


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

    public void checkHardware() {
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

    public void authenticate() {
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

        if (context != null) {
            int color = ContextCompat.getColor(context, colorRes);
            ColorStateList stateList = ColorStateList.valueOf(color);
            ivFingerprint.setBackgroundTintList(stateList);
        }
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
        Log.d(TAG, "onCancelClicked");

        if (onFingerprintDialogEventListener != null) {
            onFingerprintDialogEventListener.onFingerprintEvent(FingerprintDialogEvent.CANCEL);
        }

        closeDialog();
    }

    private void closeDialog() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    // Event overrides

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.d(TAG, "onDismiss");

        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }

        super.onDismiss(dialog);
    }

    @Override
    public void onDestroy() {

        if (cancellationSignal != null) {
            cancellationSignal.cancel();
        }

        super.onDestroy();
    }

    // Bottom Sheet Dialog Callbacks

    public class BottomSheetCallback extends BottomSheetBehavior.BottomSheetCallback {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

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
