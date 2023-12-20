package mintlin.io.encryption

import java.security.Key
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

internal actual val rsaCipherFactory = object : RSACipherFactory {
    override fun createCipher() = object : RSACipher {
        val keypair = KeyPairGenerator.getInstance("RSA")
            .also { it.initialize(2048) }.genKeyPair()

        override val publicKey: ByteArray = keypair.public.encoded
        override val privateKey: ByteArray = keypair.private.encoded

        override val verifyToken: ByteArray = ByteArray(4).apply { SplittableRandom().nextBytes(this) }

        override fun decrypt(data: ByteArray): ByteArray =
            encrypt(Cipher.DECRYPT_MODE, keypair.private, data)

        override fun encrypt(data: ByteArray) =
            encrypt(Cipher.ENCRYPT_MODE, keypair.public, data)
        
        private fun encrypt(mode: Int, key: Key, data: ByteArray) =
            Cipher.getInstance("RSA/ECB/PKCS1Padding")
                .also { it.init(mode, key) }.doFinal(data)
    }
}

internal actual val aesCipherFactory = object : AESCipherFactory {
    override fun createAESCipher() = object : AESCipher {
        lateinit var javaSecretKey: JavaSecretKey

        override fun setSecretKey(data: ByteArray) {
            javaSecretKey = JavaSecretKey(data)
        }

        override fun decrypt(data: ByteArray) = javaSecretKey.decryptionKey.update(data)

        override fun encrypt(data: ByteArray) = javaSecretKey.encryptionKey.update(data)
    }

    inner class JavaSecretKey(secretKey: ByteArray) {
        val javaSecretKey = SecretKeySpec(secretKey, "AES")
        val encryptionKey = getCipher(Cipher.ENCRYPT_MODE, javaSecretKey)
        val decryptionKey = getCipher(Cipher.DECRYPT_MODE, javaSecretKey)
        private fun getCipher(mode: Int, key: Key) =
            Cipher.getInstance("AES/CFB8/NoPadding")
                .apply { init(mode, key, IvParameterSpec(key.encoded)) }
    }
}

actual val digester = object : Digester {
    override fun digest(data: String, publicKey: ByteArray, secretKey: ByteArray): ByteArray? {
        return try {
            val digest = MessageDigest.getInstance("SHA-1")
            digest.update(data.toByteArray(charset("ISO_8859_1")))
            digest.update(secretKey)
            digest.update(publicKey)
            digest.digest()
        } catch (_: Throwable) { null }
    }

}