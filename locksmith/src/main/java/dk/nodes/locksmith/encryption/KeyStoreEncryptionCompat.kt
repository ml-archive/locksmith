package dk.nodes.locksmith.encryption

import android.content.Context
import android.os.Build
import android.security.KeyPairGeneratorSpec
import dk.nodes.locksmith.encryption.EncryptionHelper.Companion.MODE
import dk.nodes.locksmith.encryption.EncryptionHelper.Companion.PROVIDER
import dk.nodes.locksmith.encryption.EncryptionHelper.Companion.RSA_BIT_LENGTH
import dk.nodes.locksmith.encryption.EncryptionHelper.Companion.RSA_KEY_ALIAS
import dk.nodes.locksmith.encryption.EncryptionHelper.Companion.RSA_MODE
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.util.*
import javax.crypto.Cipher
import javax.security.auth.x500.X500Principal

class KeyStoreEncryptionCompat(private val context: Context) : EncryptionHelper {
    private var rsaPrivate: PrivateKey? = null
    private var rsaPublic: PublicKey? = null

    init {
        generateRsaKeys()
    }

    override fun generateRsaKeys() {
        val keyStore: KeyStore

        try {
            keyStore = KeyStore.getInstance(PROVIDER)
            keyStore.load(null)
        } catch (ignored: Exception) {
            return
        }

        // Generate the RSA key pairs
        if (!keyStore.containsAlias(RSA_KEY_ALIAS)) {
            // Generate a key pair for encryption
            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 30)

            val spec = KeyPairGeneratorSpec.Builder(context)
                    .setAlias(RSA_KEY_ALIAS)
                    .setKeySize(RSA_BIT_LENGTH)
                    .setSubject(X500Principal("CN=" + RSA_KEY_ALIAS))
                    .setSerialNumber(BigInteger.TEN)
                    .setStartDate(start.time)
                    .setEndDate(end.time)
                    .build()

            val kpg = KeyPairGenerator.getInstance(MODE, PROVIDER)
            kpg.initialize(spec)
            kpg.generateKeyPair()
        }

        val rsaKey = keyStore.getEntry(RSA_KEY_ALIAS, null) as KeyStore.PrivateKeyEntry

        rsaPrivate = rsaKey.privateKey
        rsaPublic = rsaKey.certificate.publicKey
    }

    override fun encrypt(data: ByteArray): ByteArray {
        val inputCipher = getCipher()
        inputCipher.init(Cipher.ENCRYPT_MODE, rsaPublic)
        return inputCipher.doFinal(data)
    }

    override fun decrypt(data: ByteArray): ByteArray {
        val output = getCipher()
        output.init(Cipher.DECRYPT_MODE, rsaPrivate)
        return output.doFinal(data)
    }

    private fun getCipher(): Cipher {
        try {
            return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) { // below android m
                Cipher.getInstance(RSA_MODE, "AndroidOpenSSL") // error in android 6: InvalidKeyException: Need RSA private or public key
            } else { // android m and above
                Cipher.getInstance(RSA_MODE, "AndroidKeyStoreBCWorkaround") // error in android 5: NoSuchProviderException: Provider not available: AndroidKeyStoreBCWorkaround
            }
        } catch (exception: Exception) {
            throw RuntimeException("getCipher: Failed to get an instance of Cipher", exception)
        }

    }
}