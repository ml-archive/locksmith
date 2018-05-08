package dk.nodes.locksmith.core.encryption.manager;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public interface EncryptionManager {

    void init() throws LocksmithException;

    // String

    String encryptString(String data) throws LocksmithException;

    String decryptString(String data) throws LocksmithException;

    // Integer

    String encryptInt(int data) throws LocksmithException;

    int decryptInt(String data) throws LocksmithException;

    // Float

    String encryptFloat(float data) throws LocksmithException;

    float decryptFloat(String data) throws LocksmithException;

    // Long

    String encryptLong(long data) throws LocksmithException;

    long decryptLong(String data) throws LocksmithException;

    // Boolean

    String encryptBoolean(boolean data) throws LocksmithException;

    boolean decryptBoolean(String data) throws LocksmithException;
}
