package dk.nodes.locksmith

import android.os.Build
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricPrompt
import dk.nodes.locksmith.core.exceptions.LocksmithException
import java.io.IOException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.UnrecoverableKeyException
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

@RequiresApi(api = Build.VERSION_CODES.M)
class BiometricCryptManager {
    private var keyStore: KeyStore? = null
    private var cipher: Cipher? = null
    private lateinit var cryptoObject: BiometricPrompt.CryptoObject

    private fun generateKey() {
        keyStore = KeyStore.getInstance("AndroidKeyStore")

        keyStore?.load(null)
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            "AndroidKeyStore"
        )
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KeyBiometric,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                .setUserAuthenticationRequired(true)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                .build()
        )
        keyGenerator.generateKey()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            generateSecretKey(
                KeyGenParameterSpec.Builder(
                    KeyBiometric,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
                )
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .setUserAuthenticationRequired(true)
                    // Invalidate the keys if the user has registered a new biometric
                    // credential, such as a new fingerprint. Can call this method only
                    // on Android 7.0 (API level 24) or higher. The variable
                    // "invalidatedByBiometricEnrollment" is true by default.
                    .setInvalidatedByBiometricEnrollment(true)
                    .build()
            )
        }
    }

    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore"
        )
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }

    @Throws(
        InvalidKeyException::class,
        UnrecoverableKeyException::class,
        NoSuchAlgorithmException::class,
        KeyStoreException::class,
        IOException::class,
        CertificateException::class
    )
    private fun generateCipher() { // Load our key from our keystore
        keyStore!!.load(null)
        val key =
            keyStore!!.getKey(KeyBiometric, null) as SecretKey
        // Start our cypher
        cipher!!.init(Cipher.ENCRYPT_MODE, key)
    }

    private fun generateCypherObject() {
        cryptoObject = BiometricPrompt.CryptoObject(cipher!!)
    }

    companion object {
        private val TAG = BiometricCryptManager::class.java.simpleName
        private val KeyBiometric = "LockSmithBiometricKey"
    }

    init {
        try {
            generateKey()
            getCipher()
            generateCipher()
            generateCypherObject()
        } catch (e: Exception) {
            throw LocksmithException(LocksmithException.Type.Initiation, e)
        }
    }


    fun getCryptoObject(): BiometricPrompt.CryptoObject {
        return cryptoObject
    }

    private fun getSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(KeyBiometric, null) as SecretKey
    }

    private fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
            .also { this.cipher = it }
    }
}