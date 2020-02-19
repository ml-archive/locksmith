package dk.nodes.locksmith.core.exceptions;

import androidx.annotation.NonNull;

public class LocksmithException extends Exception {
    @NonNull
    public final Type type;

    public LocksmithException(Type type, Exception exception) {
        super(type.toString(), exception);
        this.type = type;
    }

    public LocksmithException(Type type) {
        super(type.toString());
        this.type = type;
    }

    @NonNull
    public Type getType() {
        return type;
    }

    public enum Type {
        /**
         * Will return this type of error if initiation failed
         */
        Initiation,
        /**
         * Will return this type if the cipher/algorithm was not properly initiated
         */
        Uninitiated,
        /**
         * Will return this type if key has expired (will usually require you to go through the fingerprint validation sequence again)
         */
        Unauthenticated,
        /**
         * Will return this type if the data fed to the encrypt method isn't a valid encrypted message
         */
        InvalidData,
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
