package dk.nodes.locksmith.core.encryption.providers;

import java.nio.charset.Charset;
import java.security.Key;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public class AesPasswordKeyProviderImpl implements KeyProvider {
    private Charset charSet = Charset.forName("UTF-8");
    private String keyType = "AES";
    private String hashAlgorithm = "SHA-256";
    private String cipherType = "AES/GCM/NoPadding";
    private String password;
    private SecretKeySpec secretKeySpec;

    public AesPasswordKeyProviderImpl(String password) {
        this.password = password;
    }

    @Override
    public void init() throws LocksmithException {
        try {
            MessageDigest digest = MessageDigest.getInstance(hashAlgorithm);
            byte[] bytes = password.getBytes(charSet);
            digest.update(bytes);
            byte[] key = digest.digest();
            secretKeySpec = new SecretKeySpec(key, keyType);
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

    @Override
    public Key getKey() throws LocksmithException {
        if (secretKeySpec == null) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated);
        }

        return secretKeySpec;
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
