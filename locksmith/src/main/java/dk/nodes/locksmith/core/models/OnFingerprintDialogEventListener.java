package dk.nodes.locksmith.core.models;

import android.support.annotation.NonNull;

public interface OnFingerprintDialogEventListener {
    void onFingerprintEvent(@NonNull FingerprintDialogEvent event);
}
