package dk.nodes.locksmith

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import dk.nodes.locksmith.core.encryption.handlers.EncryptionHandler
import dk.nodes.locksmith.core.encryption.manager.EncryptionManagerImpl
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

class BiometricEncryptionManager(encryptionHandler: EncryptionHandler) : EncryptionManagerImpl(encryptionHandler) {
    private fun generateSecretKey(keyGenParameterSpec: KeyGenParameterSpec) {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        keyGenerator.init(keyGenParameterSpec)
        keyGenerator.generateKey()
    }


}