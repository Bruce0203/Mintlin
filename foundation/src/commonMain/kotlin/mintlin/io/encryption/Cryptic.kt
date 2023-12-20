package mintlin.io.encryption

fun createRSACipher(): RSACipher = rsaCipherFactory.createCipher()
fun createAESCipher(): AESCipher = aesCipherFactory.createAESCipher()

internal expect val rsaCipherFactory: RSACipherFactory
internal expect val aesCipherFactory: AESCipherFactory

expect val digester: Digester

interface RSACipherFactory {
    fun createCipher(): RSACipher
}

interface AESCipherFactory {
    fun createAESCipher(): AESCipher
}

interface Cipher {
    fun decrypt(data: ByteArray): ByteArray
    fun encrypt(data: ByteArray): ByteArray
}

interface RSACipher : Cipher {
    val publicKey: ByteArray
    val privateKey: ByteArray
    val verifyToken: ByteArray
}

interface AESCipher : Cipher {
    fun setSecretKey(data: ByteArray)
}

interface Digester {
    fun digest(data: String, publicKey: ByteArray, secretKey: ByteArray): ByteArray?
}
