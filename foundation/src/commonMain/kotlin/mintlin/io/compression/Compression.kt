package mintlin.io.compression

expect val zLibCompression: Compression

expect val gZipCompression: Compression

interface Compression {
    fun compress(input: ByteArray): ByteArray
    fun decompress(input: ByteArray): ByteArray
    companion object {
        val emptyCompression = object : Compression {
            override fun compress(input: ByteArray) = input
            override fun decompress(input: ByteArray) = input
        }
    }
}
