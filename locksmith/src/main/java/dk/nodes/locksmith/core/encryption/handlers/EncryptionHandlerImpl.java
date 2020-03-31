package dk.nodes.locksmith.core.encryption.handlers;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.spec.IvParameterSpec;

import dk.nodes.locksmith.core.encryption.providers.KeyProvider;
import dk.nodes.locksmith.core.exceptions.LocksmithException;
import dk.nodes.locksmith.core.models.EncryptionData;

public class EncryptionHandlerImpl implements EncryptionHandler {
    private boolean isInitialized = false;
    private KeyProvider keyProvider;

    public EncryptionHandlerImpl(KeyProvider keyProvider) {
        this.keyProvider = keyProvider;
    }

    @Override
    public void init() throws LocksmithException {
        keyProvider.init();
        isInitialized = true;
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    //Main Decrypting Methods

    @Override
    public String encrypt(byte[] data) throws LocksmithException {
        byte[] iv = getIV();
        EncryptionData encryptedData = encrypt(data, iv);
        return encryptedData.encode();
    }

    @Override
    public byte[] decrypt(String data) throws LocksmithException {
        EncryptionData encryptedData = new EncryptionData(data);
        return decrypt(encryptedData.data, encryptedData.iv);
    }

    private byte[] getIV() {
        byte[] iv;
        iv = new byte[16];
        SecureRandom rng = new SecureRandom();
        rng.nextBytes(iv);
        return iv;
    }

    private EncryptionData encrypt(byte[] data, byte[] iv) throws LocksmithException {
        try {
            Cipher cipher = keyProvider.getCipher();
            Key key = keyProvider.getKey();

            cipher.init(Cipher.ENCRYPT_MODE, key);

            EncryptionData result = new EncryptionData();
            result.iv = cipher.getIV();
            result.data = cipher.doFinal(data);

            return result;
        } catch (NullPointerException | InvalidKeyException | BadPaddingException e) {
            if (keyProvider.getKey() == null) {
                throw new LocksmithException(LocksmithException.Type.Uninitiated, e);
            } else {
                throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
            }
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithException(LocksmithException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
            }
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Generic, e);
        }
    }


    private byte[] decrypt(byte[] data, byte[] iv) throws LocksmithException {
        try {
            Cipher cipher = keyProvider.getCipher();
            Key key = keyProvider.getKey();

            cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(iv));

            return cipher.doFinal(data);
        } catch (NullPointerException e) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated, e);
        } catch (InvalidKeyException | BadPaddingException e) {
            throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
        } catch (IllegalBlockSizeException e) {
            if (e.getCause() instanceof KeyStoreException) {
                throw new LocksmithException(LocksmithException.Type.Unauthenticated, e);
            } else {
                throw new LocksmithException(LocksmithException.Type.EncryptionError, e);
            }
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Generic, e);
        }
    }
}
