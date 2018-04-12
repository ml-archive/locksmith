package dk.nodes.locksmith.exceptions;

import android.support.annotation.NonNull;

public class LocksmithEncryptionException extends Exception {
    public enum Type {
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
        IllegalBlockSize,
        /**
         * Will return this type if the selected padding is incorrect
         */
        BadPadding,
        /**
         * Will return this type if the key provided is the wrong one
         */
        InvalidKey,
        /**
         * Will return this type if the algorithm selected is invalid
         */
        InvalidAlgorithm,
        /**
         * Will return this type if the cipher was not properly initiated
         */
        UninitiatedCipher,
        Generic
    }

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
}
