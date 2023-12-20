package mintlin.serializer

import kotlinx.io.Buffer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.lang.classNameOf
import mintlin.serializer.VarIntMax2BytesSerializer.readVarIntMax2Bytes
import mintlin.serializer.VarIntMax2BytesSerializer.writeVarIntMax2Bytes
import mintlin.serializer.VarIntSerializer.readVarInt
import mintlin.serializer.VarIntSerializer.writeVarInt

object VarString16Serializer : VarStringSerializer(maxLength = 16)
typealias VarString16 = @Serializable(VarString16Serializer::class) String

object VarString20Serializer : VarStringSerializer(maxLength = 20)
typealias VarString20 = @Serializable(VarString20Serializer::class) String

object VarString40Serializer : VarStringSerializer(maxLength = 40)
typealias VarString40 = @Serializable(VarString40Serializer::class) String

object VarString128Serializer : VarStringSerializer(maxLength = 128)
typealias VarString128 = @Serializable(VarString128Serializer::class) String

object VarString255Serializer : VarStringSerializer(maxLength = 255)
typealias VarString255 = @Serializable(VarString255Serializer::class) String

object VarString256Serializer : VarStringSerializer(maxLength = 256)
typealias VarString256 = @Serializable(VarString256Serializer::class) String

object VarString384Serializer : VarStringSerializer(maxLength = 384)
typealias VarString384 = @Serializable(VarString384Serializer::class) String

object VarString32500Serializer : VarStringSerializer(maxLength = 32500)
typealias VarString32500 = @Serializable(VarString32500Serializer::class) String

object VarString32767Serializer : VarStringSerializer(maxLength = 32767)
typealias VarString32767 = @Serializable(VarString32767Serializer::class) String

object VarString262144Serializer : VarStringSerializer(maxLength = 262144)
typealias VarString262144 = @Serializable(VarString262144Serializer::class) String


open class VarStringSerializer(val maxLength: Int) : KSerializer<String> {
    override val descriptor = buildClassSerialDescriptor("VarString$maxLength")

    override fun deserialize(decoder: Decoder) = decoder.getBuffer().readVarString(maxLength = maxLength)

    override fun serialize(encoder: Encoder, value: String) = encoder.getBuffer().writeVarString(value)

    fun Buffer.readVarString(maxLength: Int): String {
        val length: Int = readVarInt()
        require(length <= maxLength * 3) { "String is longer than maximum allowed length" }
        val string = ByteArray(length) { readByte() }.decodeToString()
        require(string.length <= maxLength) { "String is longer than maximum allowed length" }
        return string
    }

    fun Buffer.writeVarString(value: String) {
        writeVarInt(value.length)
        value.encodeToByteArray().forEach { writeByte(it) }
    }

}

object VarString32767Max2Bytes : VarStringOfLengthMax2Bytes(32767)

open class VarStringOfLengthMax2Bytes(private val length: Int) : KSerializer<String> {
    override val descriptor = buildClassSerialDescriptor("${classNameOf<VarStringOfLengthMax2Bytes>()}$length")

    override fun deserialize(decoder: Decoder) =
        decoder.getBuffer().readVarStringOfLengthMax2Bytes(length)

    override fun serialize(encoder: Encoder, value: String) =
        encoder.getBuffer().writeVarStringOfLengthMax2Bytes(value)

    fun Buffer.readVarStringOfLengthMax2Bytes(maxLength: Int): String {
        val length: Int = readVarIntMax2Bytes()
        require(length <= maxLength * 3) { "String is longer than maximum allowed length" }
        val string = ByteArray(length) { readByte() }.decodeToString()
        require(string.length <= maxLength) { "String is longer than maximum allowed length" }
        return string
    }

    fun Buffer.writeVarStringOfLengthMax2Bytes(value: String) {
        writeVarIntMax2Bytes(value.length)
        value.encodeToByteArray().forEach { writeByte(it) }
    }
}
