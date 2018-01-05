package dk.nodes.locksmith.encryption


interface EncryptionHelper {
    companion object {
        val RSA_BIT_LENGTH = 2048
        val MODE = "RSA"
        val RSA_KEY_ALIAS = "AndroidEncryptionKey"
        val RSA_MODE = "RSA/ECB/PKCS1Padding"
        val PROVIDER = "AndroidKeyStore"
    }

    fun generateRsaKeys()

    fun encrypt(data: ByteArray): ByteArray

    fun decrypt(data: ByteArray): ByteArray
}