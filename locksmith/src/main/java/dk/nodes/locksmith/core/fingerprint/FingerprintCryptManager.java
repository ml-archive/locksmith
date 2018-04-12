package dk.nodes.locksmith.core.fingerprint;

import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import dk.nodes.locksmith.core.exceptions.CipherCreationException;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintCryptManager {
    private static final String TAG = FingerprintCryptManager.class.getSimpleName();

    private KeyStore keyStore;
    private Cipher cipher;
    private FingerprintManager.CryptoObject cryptoObject;

    private String KEY_NAME_FINGERPRINT = "LockSmithFingerprintKey";

    public FingerprintCryptManager() throws CipherCreationException {
        try {
            generateKey();
            getCipher();
            generateCipher();
            generateCypherObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new CipherCreationException();
        }
    }

    private void generateKey() throws InvalidAlgorithmParameterException, CertificateException, NoSuchAlgorithmException, IOException, KeyStoreException, NoSuchProviderException {
        keyStore = KeyStore.getInstance("AndroidKeyStore");
        keyStore.load(null);

        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore");

        keyGenerator.init(new KeyGenParameterSpec.Builder(KEY_NAME_FINGERPRINT, KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build());

        keyGenerator.generateKey();
    }

    private void getCipher() throws NoSuchPaddingException, NoSuchAlgorithmException {
        cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7);
    }

    private void generateCipher() throws InvalidKeyException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException {
        // Load our key from our keystore
        keyStore.load(null);
        SecretKey key = (SecretKey) keyStore.getKey(KEY_NAME_FINGERPRINT, null);
        // Start our cypher
        cipher.init(Cipher.ENCRYPT_MODE, key);
    }

    private void generateCypherObject() {
        cryptoObject = new FingerprintManager.CryptoObject(cipher);
    }

    public FingerprintManager.CryptoObject getCryptoObject() {
        return cryptoObject;
    }
}
