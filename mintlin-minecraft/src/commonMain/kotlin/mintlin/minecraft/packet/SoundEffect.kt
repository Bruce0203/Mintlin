package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.minecraft.datastructure.IdentifierSerializer
import mintlin.minecraft.datastructure.Point3D
import mintlin.serializer.NullableFloatSerializer
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf

@Serializable
data class SoundEffect(
    val soundIdentifier: SoundIdentifier,
    val category: SoundCategory,
    val effectPosition: Point3D,
    val volume: Float,
    val pitch: Float,
    val seed: Long
)

@Serializable(SoundIdentifier.Serializer::class)
sealed interface SoundIdentifier {
    data class Id(val id: Int) : SoundIdentifier

    data class Named(
        val soundId: Int,
        val name: String,
        val fixedRange: Float?
    ) : SoundIdentifier

    companion object Serializer : KSerializer<SoundIdentifier> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<SoundIdentifier>())

        override fun deserialize(decoder: Decoder): SoundIdentifier {
            var soundId = VarIntSerializer.deserialize(decoder)
            return if (soundId++ != 0) Id(soundId)
            else Named(
                soundId = soundId,
                name = IdentifierSerializer.deserialize(decoder),
                fixedRange = NullableFloatSerializer.deserialize(decoder)
            )
        }

        override fun serialize(encoder: Encoder, value: SoundIdentifier) {
            VarIntSerializer.serialize(
                encoder, when (value) {
                    is Id -> value.id
                    is Named -> value.soundId
                }
            )
            when (value) {
                is Id -> {}
                is Named -> {
                    IdentifierSerializer.serialize(encoder, value.name)
                    NullableFloatSerializer.serialize(encoder, value.fixedRange)
                }
            }
        }

    }
}