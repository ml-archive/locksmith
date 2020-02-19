package dk.nodes.locksmith

import android.security.keystore.KeyProperties
import dk.nodes.locksmith.core.encryption.providers.KeyProvider
import java.security.Key
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.SecretKey

object BiometricKeyProvider : KeyProvider {
    override fun getCipher(): Cipher {
        return Cipher.getInstance(
            KeyProperties.KEY_ALGORITHM_AES + "/"
                + KeyProperties.BLOCK_MODE_CBC + "/"
                + KeyProperties.ENCRYPTION_PADDING_PKCS7
        )
    }

    override fun getKey(): Key {
        val keyStore = KeyStore.getInstance("AndroidKeyStore")

        // Before the keystore can be accessed, it must be loaded.
        keyStore.load(null)
        return keyStore.getKey(keyPassword, "".toCharArray()) as SecretKey
    }

    override fun init() {}

    private const val keyPassword = "adafaq"
}