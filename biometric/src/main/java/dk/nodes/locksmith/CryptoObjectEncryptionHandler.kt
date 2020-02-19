package dk.nodes.locksmith

import dk.nodes.locksmith.core.encryption.handlers.EncryptionHandlerImpl

object CryptoObjectEncryptionHandler : EncryptionHandlerImpl(BiometricKeyProvider) {
    override fun isInitialized(): Boolean = true

    override fun encrypt(data: ByteArray?): String {
    }

    override fun decrypt(data: String?): ByteArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun init() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}