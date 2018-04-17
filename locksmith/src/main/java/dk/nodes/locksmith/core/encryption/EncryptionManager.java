package dk.nodes.locksmith.core.encryption;

import android.content.Context;
import android.os.Build;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

import dk.nodes.locksmith.core.Locksmith;
import dk.nodes.locksmith.core.encryption.handlers.CompatEncryptionHandlerImpl;
import dk.nodes.locksmith.core.encryption.handlers.EncryptionHandlerImpl;
import dk.nodes.locksmith.core.encryption.handlers.FingerprintEncryptionHandlerImpl;
import dk.nodes.locksmith.core.exceptions.LocksmithCreationException;
import dk.nodes.locksmith.core.exceptions.LocksmithEncryptionException;

public class EncryptionManager {
    private Charset charset = Charset.forName("UTF-8");
    private EncryptionHandler encryptionHandler;

    public void init(Context context) throws LocksmithCreationException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Locksmith.getInstance().isUseFingerprint()) {
                // If we enabled fingerprint auth then lets set our handler to that
                encryptionHandler = new FingerprintEncryptionHandlerImpl();
            } else {
                // If we didn't then lets just use keystore encryption
                encryptionHandler = new EncryptionHandlerImpl();
            }
        } else {
            // If we're using below Api 23 then we need to fall back to a compat encryption version
            encryptionHandler = new CompatEncryptionHandlerImpl(context);
        }

        encryptionHandler.init();
    }

    private EncryptionHandler getEncryptionHandler() throws LocksmithEncryptionException {
        if (encryptionHandler == null) {
            throw new LocksmithEncryptionException(LocksmithEncryptionException.Type.Uninitiated);
        }
        return encryptionHandler;
    }

    // String

    public String encryptString(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = data.getBytes(charset);
        return getEncryptionHandler().encrypt(decryptedData);
    }

    public String decryptString(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        return new String(decryptedData, charset);
    }

    // Integer

    public String encryptInt(int data) throws LocksmithEncryptionException {
        byte[] decryptedData = ByteBuffer.allocate(4).putInt(data).array();
        return getEncryptionHandler().encrypt(decryptedData);
    }

    public int decryptInt(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        return ByteBuffer.wrap(decryptedData).getInt();
    }

    // Float

    public String encryptFloat(float data) throws LocksmithEncryptionException {
        byte[] decryptedData = ByteBuffer.allocate(4).putFloat(data).array();
        return getEncryptionHandler().encrypt(decryptedData);
    }

    public float decryptFloat(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        System.out.println("Buffer Size: " + decryptedData.length);
        return ByteBuffer.wrap(decryptedData).getFloat();
    }

    // Long

    public String encryptLong(long data) throws LocksmithEncryptionException {
        byte[] decryptedData = ByteBuffer.allocate(8).putLong(data).array();
        return getEncryptionHandler().encrypt(decryptedData);
    }

    public long decryptLong(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        System.out.println("Buffer Size: " + decryptedData.length);
        return ByteBuffer.wrap(decryptedData).getLong();
    }

    // Boolean

    public String encryptBoolean(boolean data) throws LocksmithEncryptionException {
        int dataInt = data ? 1 : 0;
        byte[] decryptedData = new byte[]{(byte) dataInt};
        return getEncryptionHandler().encrypt(decryptedData);
    }

    public boolean decryptBoolean(String data) throws LocksmithEncryptionException {
        byte[] decryptedData = getEncryptionHandler().decrypt(data);
        int dataInt = (int) decryptedData[0];
        return dataInt == 1;
    }
}
