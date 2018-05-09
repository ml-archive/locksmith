package dk.nodes.locksmith.core.encryption.providers;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public class AesKeyProviderImpl implements KeyProvider {
    //Lengths
    private final int AES_BIT_LENGTH = 256;
    //AES Key
    private String keyType = "AES";
    private String cipherType = "AES/GCM/NoPadding";
    //General Stuff
    private byte[] encodedKey;
    private SecretKeySpec aesKey;

    public AesKeyProviderImpl(byte[] encodedKey) {
        this.encodedKey = encodedKey;
    }

    public AesKeyProviderImpl() {
        this.encodedKey = null;
    }

    @Override
    public void init() throws LocksmithException {
        try {

            if (encodedKey == null) {
                // Generate our key if we don't have it
                KeyGenerator keyGen = KeyGenerator.getInstance(keyType);
                keyGen.init(AES_BIT_LENGTH);
                SecretKey sKey = keyGen.generateKey();
                encodedKey = sKey.getEncoded();
            }

            aesKey = new SecretKeySpec(encodedKey, keyType);

        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

    @Override
    public Key getKey() throws LocksmithException {

        if (aesKey == null) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated);
        }

        return aesKey;
    }

    @Override
    public Cipher getCipher() throws LocksmithException {
        try {
            return Cipher.getInstance(cipherType);
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }
}
