package dk.nodes.nencrypt.models

import android.util.Base64
import dk.nodes.nencrypt.exceptions.InvalidEncryptionDataException
import java.util.*

data class EncryptionData constructor(var iv: ByteArray, var data: ByteArray) {
    private val deliminator: String = "_"

    /**
     * Used for turning out base64 encoded strings back into encryption data for decrypting
     * @throws InvalidEncryptionDataException will throw an error if the format isn't correct
     */
    @Throws(InvalidEncryptionDataException::class)
    constructor(stringData: String) : this(iv = ByteArray(0), data = ByteArray(0)) {
        val splitData = stringData.split(deliminator.toRegex())
        if (splitData.size == 2) {
            this.iv = Base64.decode(splitData[0], Base64.NO_WRAP)
            this.data = Base64.decode(splitData[1], Base64.NO_WRAP)
        } else {
            throw InvalidEncryptionDataException()
        }
    }

    /**
     * Turn our byte data into a base64 String split by our deliminator
     */
    fun encode(): String = Base64.encodeToString(iv, Base64.NO_WRAP) + deliminator + Base64.encodeToString(data, Base64.NO_WRAP)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionData

        if (!Arrays.equals(iv, other.iv)) return false
        if (!Arrays.equals(data, other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = Arrays.hashCode(iv)
        result = 31 * result + Arrays.hashCode(data)
        return result
    }

    override fun toString(): String = String.format("IV: %s - Data: %s", iv.size, data.size)
}
