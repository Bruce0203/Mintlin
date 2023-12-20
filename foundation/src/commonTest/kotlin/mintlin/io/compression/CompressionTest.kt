package mintlin.io.compression

import io.kotest.core.spec.style.StringSpec

class CompressionTest : StringSpec({
    "compression" {
        val zLib = zLibCompression
        val bytes = ByteArray(256) { (0..1000).random().toByte() }
        repeat(10) {
            val compressed = zLib.compress(bytes)
//        println(compressed.toList())
            val decompressed = zLib.decompress(compressed).toList()
        }
        println(bytes.toList())
        val compressed = zLib.compress(bytes)
//        println(compressed.toList())
        val decompressed = zLib.decompress(compressed).toList()
        println(decompressed)
    }
})