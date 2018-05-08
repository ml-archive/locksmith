package dk.nodes.locksmith.core.encryption.manager;

import android.content.Context;
import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import dk.nodes.locksmith.core.encryption.handlers.EncryptionHandler;
import dk.nodes.locksmith.core.encryption.handlers.EncryptionHandlerImpl;
import dk.nodes.locksmith.core.encryption.providers.AndroidKeyProviderImpl;
import dk.nodes.locksmith.core.encryption.providers.CompatAndroidKeyProviderImpl;
import dk.nodes.locksmith.core.encryption.providers.KeyProvider;
import dk.nodes.locksmith.core.exceptions.LocksmithException;

public class EncryptionManagerImpl implements EncryptionManager {
    private Charset charset = Charset.forName("UTF-8");
    private EncryptionHandler encryptionHandler;
    private boolean useFingerprint;

    public EncryptionManagerImpl(EncryptionHandler encryptionHandler) {
        this.encryptionHandler = encryptionHandler;
    }

    public EncryptionManagerImpl(Context context, boolean useFingerprint) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Using higher than M then we should use the non compat version
            KeyProvider keyProvider = new AndroidKeyProviderImpl(useFingerprint);
            encryptionHandler = new EncryptionHandlerImpl(keyProvider);
        } else {
            // If we're using below Api 23 then we need to fall back to a compat encryption version
            KeyProvider keyProvider = new CompatAndroidKeyProviderImpl(context);
            encryptionHandler = new EncryptionHandlerImpl(keyProvider);
        }

        this.useFingerprint = useFingerprint;
    }

    @Override
    public void init() throws LocksmithException {
        encryptionHandler.init();
    }

    private EncryptionHandler getEncryptionHandler() throws LocksmithException {
        if (encryptionHandler == null) {
            throw new LocksmithException(LocksmithException.Type.Uninitiated);
        }

        if (!useFingerprint && !encryptionHandler.isInitialized()) {
            encryptionHandler.init();
        }

        return encryptionHandler;
    }

    // String
    @Override
    public String encryptString(String data) throws LocksmithException {
        byte[] decryptedData = data.getBytes(charset);
        return getEncryptionHandler().encrypt(decryptedData);
    }

    @Override
    public String decryptString(String data) throws LocksmithException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        return new String(decryptedData, charset);
    }

    // Integer
    @Override
    public String encryptInt(int data) throws LocksmithException {
        byte[] decryptedData = ByteBuffer.allocate(4).putInt(data).array();
        return getEncryptionHandler().encrypt(decryptedData);
    }

    @Override
    public int decryptInt(String data) throws LocksmithException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        return ByteBuffer.wrap(decryptedData).getInt();
    }

    // Float
    @Override
    public String encryptFloat(float data) throws LocksmithException {
        byte[] decryptedData = ByteBuffer.allocate(4).putFloat(data).array();
        return getEncryptionHandler().encrypt(decryptedData);
    }

    @Override
    public float decryptFloat(String data) throws LocksmithException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        return ByteBuffer.wrap(decryptedData).getFloat();
    }

    // Long
    @Override
    public String encryptLong(long data) throws LocksmithException {
        byte[] decryptedData = ByteBuffer.allocate(8).putLong(data).array();
        return getEncryptionHandler().encrypt(decryptedData);
    }

    @Override
    public long decryptLong(String data) throws LocksmithException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        return ByteBuffer.wrap(decryptedData).getLong();
    }

    // Boolean
    @Override
    public String encryptBoolean(boolean data) throws LocksmithException {
        int dataInt = data ? 1 : 0;
        byte[] decryptedData = new byte[]{(byte) dataInt};
        return getEncryptionHandler().encrypt(decryptedData);
    }

    @Override
    public boolean decryptBoolean(String data) throws LocksmithException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        int dataInt = (int) decryptedData[0];
        return dataInt == 1;
    }
}
