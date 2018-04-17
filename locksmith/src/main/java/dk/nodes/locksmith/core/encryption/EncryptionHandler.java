package dk.nodes.locksmith.core.encryption;

import dk.nodes.locksmith.core.exceptions.LocksmithCreationException;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;

public interface EncryptionHandler {
    void init() throws LocksmithCreationException;

    String encrypt(byte[] data) throws LocksmithEncryptionException;

    byte[] decrypt(String data) throws LocksmithEncryptionException;
}
