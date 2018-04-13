package dk.nodes.locksmith.core.exceptions;

import android.support.annotation.NonNull;

public class LocksmithEncryptionException extends Exception {
    @NonNull
    public final Type type;

    public LocksmithEncryptionException(Type type, Exception exception) {
        super(type.toString(), exception);
        this.type = type;
    }

    public LocksmithEncryptionException(Type type) {
        super(type.toString());
        this.type = type;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public enum Type {
        // Initiation Errors
        /**
         * Will return this type if the cipher/algorithm was not properly initiated
         */
        Uninitiated,
        // Key Error
        /**
         * Will return this type if key has expired (will usually require you to go through the fingerprint validation sequence again)
         */
        Unauthenticated,
        // Data Errors
        /**
         * Will return this type if the data fed to the encrypt method isn't a valid encrypted message
         */
        InvalidData,
        // Algorithm Errors
        /**
         * Will return this type if the data is too long or the wrong size
         */
        EncryptionError,
        /**
         * Thrown when an unknown error is caught
         */
        Generic
    }
}
