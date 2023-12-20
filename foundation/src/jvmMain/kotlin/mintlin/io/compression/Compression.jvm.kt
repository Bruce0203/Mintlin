package mintlin.io.compression

import java.nio.ByteBuffer
import java.util.zip.Deflater
import java.util.zip.Inflater

actual val zLibCompression: Compression get() = object : Compression {

    override fun compress(input: ByteArray): ByteArray {
        val compressor = Deflater()
        val output = ByteBuffer.allocate(4096)  //FIXME cache it
        compressor.setInput(input)
        compressor.finish()
        output.clear()
        val length = compressor.deflate(output)
        compressor.end()
        return output.array().copyOf(length)
    }

    override fun decompress(input: ByteArray): ByteArray {
        val decompressor = Inflater()
        decompressor.setInput(input)
        val output = ByteBuffer.allocate(4096*8)  //FIXME cache it
        val length = decompressor.inflate(output)
        decompressor.end()
        return output.array().copyOf(length)
    }
}

actual val gZipCompression: Compression
    get() = TODO("Not yet implemented")