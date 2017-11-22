package dk.nodes.nencrypt.utils

import dk.nodes.nencrypt.BuildConfig
import java.io.UnsupportedEncodingException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


class HashingUtils {
    companion object {
        fun doHash(text: String, CHAR_SET: String): String {
            try {
                val digest = MessageDigest.getInstance("SHA-256")

                val result = digest.digest(text.toByteArray(charset(CHAR_SET)))

                val sb = StringBuilder()

                for (b in result) {
                    sb.append(String.format("%02X", b))
                }

                return sb.toString()
            } catch (e: NoSuchAlgorithmException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                return text
            } catch (e: UnsupportedEncodingException) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace()
                }
                return text
            }
        }
    }
}