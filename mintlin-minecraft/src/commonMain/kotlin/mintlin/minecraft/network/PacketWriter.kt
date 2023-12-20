package mintlin.minecraft.network

import kotlinx.io.Buffer
import kotlinx.io.readByteArray
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import mintlin.CachedPacketHolder
import mintlin.RawPacket
import mintlin.format.packet.PacketFormat
import mintlin.io.compression.Compression
import mintlin.io.encryption.AESCipher
import mintlin.io.network.SocketWriter
import mintlin.io.network.protocol.PacketRegistryDelegate
import mintlin.logger.Logger
import mintlin.serializer.VarIntSerializer.writeVarInt

class PacketWriter(
    private val rawPacketWriter: RawPacketWriter,
    private val logger: Logger,
    private val packetChannel: PacketChannel,
    packetRegistryDelegate: PacketRegistryDelegate
) {
    private val packetRegistry by packetRegistryDelegate

    fun sendCachedPackets(vararg caches: CachedPacketHolder<*>) {
        caches.forEach { sendCachedPacket(it) }
    }

    fun <T> sendCachedPacket(cache: CachedPacketHolder<T>) {
        if (packetChannel.isScopeClosed) return
        if (cache.binary.isEmpty()) {
            val packetId = packetRegistry.getIdBySerializer(cache.serializer)
            val value = if (cache.isOld) {
                val packet = cache.factory()
                cache.value = packet
                packet
            } else cache.value
            val packetData = serialize(cache.serializer, value)
            val rawPacket = RawPacket(packetId, packetData)
            val bytes = rawPacketWriter.rawPacketToByteArray(rawPacket)
            cache.binary = bytes
            debugPacket(value)
            rawPacketWriter.writeBytes(bytes)
        } else {
            debugPacket(cache.value)
            rawPacketWriter.writeBytes(cache.binary)
        }
    }

    fun <T : Any> send(type: KSerializer<T>, value: T) {
        if (packetChannel.isScopeClosed) return
        val packetId = packetRegistry.getIdBySerializer(type)
        val packetData = serialize(type, value)
        send(RawPacket(packetId, packetData), value)
    }

    private fun debugPacket(value: Any?) {
        if (value !== null) logger.debug { "[Send]: $value" }
    }

    private fun <T> send(rawPacket: RawPacket, value: T?) {
        debugPacket(value)
        rawPacketWriter.writeBytes(rawPacketWriter.rawPacketToByteArray(rawPacket))
    }

    private fun <T> serialize(serializer: KSerializer<T>, value: T) =
        PacketFormat.encodeToByteArray(serializer, value)

    inline fun <reified T : Any> send(packet: T) = send(serializer<T>(), packet)
}

class RawPacketWriter(
    private val compression: PacketCompression,
    private val packetEncryption: PacketEncryption,
    private val socketWriter: SocketWriter,
    private val packetChannel: PacketChannel
) {
    internal fun writeBytes(bytes: ByteArray) {
        if (packetChannel.isOpen) {
            socketWriter.write(packetEncryption.encryptIfStateEnabled(bytes))
        }
    }

    internal fun rawPacketToByteArray(rawPacket: RawPacket) =
        buildPayloadByRawPacket(rawPacket).run(compression::compressPayloadIfEnabled)

    private fun buildPayloadByRawPacket(rawPacket: RawPacket): ByteArray {
        val buffer = Buffer()
        buffer.writeVarInt(rawPacket.id)
        buffer.write(rawPacket.data)
        return buffer.readByteArray()
    }
}

class PacketCompression(
    private val compression: Compression,
    private val state: CompressionState
) {
    private val threshold by state

    fun compressPayloadIfEnabled(payload: ByteArray): ByteArray {
        return (if (isCompressionRequired(payload.size)) compress(payload)
        else if (state.isCompressionEnabled) payload.appendVarIntToHead(0).appendLengthToHead()
        else payload.appendLengthToHead())
    }

    private fun isCompressionRequired(length: Int) =
        state.isCompressionEnabled && threshold <= length

    private fun compress(payload: ByteArray): ByteArray {
        val decompressedLength = payload.size
        val compressed = compression.compress(payload)
        return compressed.appendVarIntToHead(decompressedLength).appendLengthToHead()
    }

    private fun ByteArray.appendLengthToHead() = appendVarIntToHead(size)

    private fun ByteArray.appendVarIntToHead(value: Int): ByteArray {
        val buffer = Buffer()
        buffer.writeVarInt(value)
        buffer.write(this)
        return buffer.readByteArray()
    }
}

class PacketEncryption(
    private val aesCipher: AESCipher,
    encryptionState: EncryptionState
) {
    private val isEncryptionEnabled by encryptionState

    fun encryptIfStateEnabled(bytes: ByteArray): ByteArray {
        return if (isEncryptionEnabled) {
            aesCipher.encrypt(bytes)
        } else bytes
    }
}
