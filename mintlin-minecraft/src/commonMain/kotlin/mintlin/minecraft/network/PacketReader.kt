package mintlin.minecraft.network

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.KSerializer
import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.format.packet.PacketFormat
import mintlin.io.compression.Compression
import mintlin.io.encryption.AESCipher
import mintlin.io.network.SocketReadListener
import mintlin.io.network.protocol.ConnectionStateChange
import mintlin.io.network.protocol.ConnectionStateDelegate
import mintlin.io.network.protocol.PacketRegistryDelegate
import mintlin.logger.Logger
import mintlin.serializer.VAR_CONTINUE_BIT
import mintlin.serializer.VAR_SEGMENT_BITS
import mintlin.serializer.VarIntReader
import mintlin.serializer.VarIntSerializer.readVarIntOrNull

class ConnectionSocketClosedEvent

class PacketReader(
    private val packetConsumer: PacketConsumer,
    private val logger: Logger,
    private val payloadReader: RawPacketPayloadReader,
    private val packetDecryption: PacketDecryption,
    scope: Scope
) : SocketReadListener {
    private val packetRegistry by scope.get<PacketRegistryDelegate>()
    private var connectionState by scope.get<ConnectionStateDelegate>()
    private val buffer = Buffer()

    override fun onRead(data: ByteArray) {
        buffer.write(packetDecryption.decryptIfStateEnabled(data))
        do {
            val payload = payloadReader.readPayload(buffer)
                ?.let { Buffer().apply { write(it) } } ?: return
            val packetId = payload.readVarIntOrNull() ?: return
            val serializer = packetRegistry.getSerializerById(packetId)
            if (serializer === null) {
                throw NoSuchElementException("${connectionState.name}[0x${packetId.toString(16)}]")
            }
            runCatching {
                val packet = deserialize(serializer, payload)!!
                if (packet is ConnectionStateChange) {
                    connectionState = packet.connectionState
                }
                logger.debug { "[Receive]: $packet" }
                packetConsumer.consume(packet)
            }.exceptionOrNull()?.also {
                it.printStackTrace()
                println("${payload.size}/${buffer.size}")
                throw it
            }
        } while (buffer.size != 0L)
    }

    private fun deserialize(serializer: KSerializer<*>, buffer: Buffer) =
        PacketFormat.decodeFromBuffer(serializer, buffer)
}

class RawPacketPayloadReader(scope: Scope) {
    private val compression: Compression = scope.get()
    private var compressionState: CompressionState = scope.get()
    private val threshold by compressionState

    fun readPayload(buffer: Buffer): ByteArray? = with(buffer) {
        var packetLength = readPacketLengthFast() ?: return null
        if (packetLength < 0) return null
        val varIntReader = VarIntReader(buffer)
        val decompressionRequired = varIntReader.isDecompressionRequired()
        packetLength -= varIntReader.readByteCount
        return if (decompressionRequired) {
            compression.decompress(readByteArray(packetLength))
        } else readByteArray(packetLength)
    }

    private fun VarIntReader.isDecompressionRequired() =
        compressionState.isCompressionEnabled && threshold <= readDecompressedLength()

    private fun VarIntReader.readDecompressedLength() = readVarInt()  //FIXME do not move buffer pos on fail

    private fun Buffer.readPacketLengthFast(): Int? {
        var value = 0
        var position = 0
        var currentByte: Int
        var pos = 0L
        while (true) {
            if (size < pos) return null
            currentByte = get(pos++).toInt()
            value = value or (currentByte and VAR_SEGMENT_BITS shl position)
            if (currentByte and VAR_CONTINUE_BIT == 0) {
                break
            }
            position += 7
            if (position >= 32) {
                return null
            }
        }
        if (size + pos < value) {
            return null
        }
        skip(pos)
        return value
    }
}

class PacketDecryption(
    private val aesCipher: AESCipher,
    encryptionState: EncryptionState
) {
    private val isEncryptionEnabled by encryptionState

    fun decryptIfStateEnabled(bytes: ByteArray): ByteArray {
        return if (isEncryptionEnabled) {
            aesCipher.decrypt(bytes)
        } else bytes
    }
}