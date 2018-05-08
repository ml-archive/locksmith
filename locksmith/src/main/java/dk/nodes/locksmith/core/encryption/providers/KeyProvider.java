package dk.nodes.locksmith.core.encryption.providers;

import java.security.Key;

import javax.crypto.Cipher;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public interface KeyProvider {
    void init() throws LocksmithException;

    Key getKey() throws LocksmithException;

    Cipher getCipher() throws LocksmithException;
}
