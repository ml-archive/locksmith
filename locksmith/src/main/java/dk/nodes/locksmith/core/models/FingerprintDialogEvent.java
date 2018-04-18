package dk.nodes.locksmith.core.models;

public enum  FingerprintDialogEvent {
    /**
     * Is called when a user cancels the dialog
     */
    CANCEL,
    /**
     * Is called when a fingerprint is successfully accepted
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
    ERROR_ENROLLMENT,
    /**
     * Is called when the cipher fails to create
     */
    ERROR_CIPHER
}
