package dk.nodes.locksmith.core.fingerprint;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import dk.nodes.locksmith.core.models.OnFingerprintDialogEventListener;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintDialogBuilder {
    private FingerprintDialog fingerprintDialog;

    public FingerprintDialogBuilder(Context context) {
        this.fingerprintDialog = new FingerprintDialog(context);
    }

    public FingerprintDialogBuilder setEventListener(OnFingerprintDialogEventListener onFingerprintDialogEventListener) {
        fingerprintDialog.onFingerprintDialogEventListener = onFingerprintDialogEventListener;
        return this;
    }

    public FingerprintDialogBuilder setCancelText(String text) {
        fingerprintDialog.cancelButtonText = text;
        return this;
    }

    public FingerprintDialogBuilder setSuccessMessage(String message) {
        fingerprintDialog.successMessageText = message;
        return this;
    }

    public FingerprintDialogBuilder setErrorMessage(String message) {
        fingerprintDialog.errorMessageText = message;
        return this;
    }

    public FingerprintDialogBuilder setTitle(String text) {
        fingerprintDialog.titleText = text;
        return this;
    }

    public FingerprintDialogBuilder setSubtitle(String text) {
        fingerprintDialog.subtitleText = text;
        return this;
    }

    public FingerprintDialogBuilder setDescription(String text) {
        fingerprintDialog.descriptionText = text;
        return this;
    }

    public FingerprintDialogBase build() {
        return fingerprintDialog;
    }
}
