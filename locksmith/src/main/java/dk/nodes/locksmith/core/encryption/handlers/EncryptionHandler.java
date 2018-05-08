package dk.nodes.locksmith.core.encryption.handlers;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public interface EncryptionHandler {
    void init() throws LocksmithException;

    boolean isInitialized();

    String encrypt(byte[] data) throws LocksmithException;

    byte[] decrypt(String data) throws LocksmithException;
}
