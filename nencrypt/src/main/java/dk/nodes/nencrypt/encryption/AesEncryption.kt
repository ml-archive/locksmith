package dk.nodes.nencrypt.encryption

import dk.nodes.nencrypt.models.EncryptionData
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec


class AesEncryption() {
    private val TAG = AesEncryption::class.java.simpleName
    //AES Variables
    private val AES_BIT_LENGTH = 256
    private val GCM_TAG_LENGTH = 128
    private val AES_KEY_TYPE = "AES"
    private val AES_BLOCK_MODE = "GCM"
    private val AES_PADDING_TYPE = "NoPadding"
    private val AES_MODE = AES_KEY_TYPE + "/" +
            AES_BLOCK_MODE + "/" +
            AES_PADDING_TYPE
    //General Stuff
    private var aesKey: SecretKeySpec? = null
    private var CHAR_SET = charset("UTF-8")

    init {
        loadAesKeys()
    }

    private fun getIV(): ByteArray {
        val iv = ByteArray(12)
        val rng = SecureRandom()
        rng.nextBytes(iv)
        return iv
    }

    //AES Key Stuff
    private fun loadAesKeys() {
        val key: ByteArray

        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(AES_BIT_LENGTH)
        val sKey = keyGen.generateKey()
        key = sKey.encoded

        aesKey = SecretKeySpec(key, "AES")
    }

    public fun encryptString(unencryptedString: String): String {
        val stringBytes = unencryptedString.toByteArray(CHAR_SET)
        val ivBytes = getIV()
        return encrypt(stringBytes, ivBytes).encode()
    }

    public fun decryptString(encryptedString: String): String {
        val encryptedData = EncryptionData(encryptedString)
        val unencryptedBytes = decrypt(encryptedData.data, encryptedData.iv)
        return String(unencryptedBytes, CHAR_SET)
    }

    private fun encrypt(data: ByteArray, iv: ByteArray): EncryptionData {
        val cipher = Cipher.getInstance(AES_MODE)
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))

        val cipherIV = cipher.iv
        val cipherData = cipher.doFinal(data)

        return EncryptionData(cipherIV, cipherData)
    }

    private fun decrypt(data: ByteArray, iv: ByteArray): ByteArray {
        val c = Cipher.getInstance(AES_MODE)
        c.init(Cipher.DECRYPT_MODE, aesKey, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        return c.doFinal(data)
    }
}