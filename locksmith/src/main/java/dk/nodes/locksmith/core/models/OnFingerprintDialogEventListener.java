package dk.nodes.locksmith.core.models;

import androidx.annotation.NonNull;

public interface OnFingerprintDialogEventListener {
    void onFingerprintEvent(@NonNull FingerprintDialogEvent event);
}
