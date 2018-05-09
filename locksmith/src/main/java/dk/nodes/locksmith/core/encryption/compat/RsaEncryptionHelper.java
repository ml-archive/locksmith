package dk.nodes.locksmith.core.encryption.compat;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

import dk.nodes.locksmith.core.exceptions.LocksmithException;

public class RsaEncryptionHelper {
    private static final String AndroidKeyStore = "AndroidKeyStore";
    private static final String RSA_KEY_ALIAS = "SharedPreferenceEncryption";
    private static final String RSA_MODE = "RSA/ECB/PKCS1Padding";
    private static final String KEY_TYPE = "RSA";
    private Context context;
    private PrivateKey rsaPrivate;
    private PublicKey rsaPublic;
    private int RSA_BIT_LENGTH = 2048;


    public RsaEncryptionHelper(Context context) throws LocksmithException {
        this.context = context;
        loadRsaKeys();
    }

    private void loadRsaKeys() throws LocksmithException {
        try {
            KeyStore keyStore;

            keyStore = KeyStore.getInstance(AndroidKeyStore);
            keyStore.load(null);

            // Generate the RSA key pairs
            if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
                // Generate a key pair for encryption
                Calendar start = Calendar.getInstance();
                Calendar end = Calendar.getInstance();
                end.add(Calendar.YEAR, 30);

                KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(context)
                        .setAlias(RSA_KEY_ALIAS)
                        .setKeySize(RSA_BIT_LENGTH)
                        .setSubject(new X500Principal("CN=" + RSA_KEY_ALIAS))
                        .setSerialNumber(BigInteger.TEN)
                        .setStartDate(start.getTime())
                        .setEndDate(end.getTime())
                        .build();

                KeyPairGenerator kpg = KeyPairGenerator.getInstance(KEY_TYPE, AndroidKeyStore);
                kpg.initialize(spec);
                kpg.generateKeyPair();


            }

            KeyStore.PrivateKeyEntry rsaKey = (KeyStore.PrivateKeyEntry) keyStore.getEntry(RSA_KEY_ALIAS, null);

            rsaPrivate = rsaKey.getPrivateKey();
            rsaPublic = rsaKey.getCertificate().getPublicKey();
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }

    public byte[] encrypt(byte[] data) throws Exception {
        if (rsaPublic == null || rsaPrivate == null) {
            return data;
        }

        Cipher inputCipher = getCipher();
        inputCipher.init(Cipher.ENCRYPT_MODE, rsaPublic);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, inputCipher);
        cipherOutputStream.write(data);
        cipherOutputStream.close();

        return outputStream.toByteArray();
    }

    public byte[] decrypt(byte[] data) throws Exception {
        if (rsaPublic == null || rsaPrivate == null) {
            return data;
        }

        Cipher output = getCipher();
        output.init(Cipher.DECRYPT_MODE, rsaPrivate);

        CipherInputStream cipherInputStream = new CipherInputStream(new ByteArrayInputStream(data), output);

        ArrayList<Byte> values = new ArrayList<>();
        int nextByte;

        while ((nextByte = cipherInputStream.read()) != -1) {
            values.add((byte) nextByte);
        }

        byte[] bytes = new byte[values.size()];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = values.get(i);
        }
        return bytes;
    }

    private Cipher getCipher() throws LocksmithException {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
                return Cipher.getInstance(RSA_MODE, "AndroidOpenSSL"); // error in android 6: InvalidKeyException: Need RSA private or public key
            } else { // android m and above
                return Cipher.getInstance(RSA_MODE, "AndroidKeyStoreBCWorkaround"); // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
            }
        } catch (Exception e) {
            throw new LocksmithException(LocksmithException.Type.Initiation, e);
        }
    }
}
