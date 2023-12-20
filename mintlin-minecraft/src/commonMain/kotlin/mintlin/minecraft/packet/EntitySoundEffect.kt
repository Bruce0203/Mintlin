package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.minecraft.datastructure.IdentifierSerializer
import mintlin.serializer.VarIntEnum
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntEnumSerializer

@Serializable(EntitySoundEffect.Serializer::class)
data class EntitySoundEffect(
    val soundId: Int,
    val soundName: String?,
    val range: Float?,
    @Serializable(SoundCategory.Serializer::class)
    val category: SoundCategory,
    override val entityId: Int,
    val volume: Float,
    val pitch: Float,
    val seed: Long
) : IdentifiedEntity {
    companion object Serializer : KSerializer<EntitySoundEffect> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<EntitySoundEffect>())

        override fun deserialize(decoder: Decoder): EntitySoundEffect {
            val soundId = VarIntSerializer.deserialize(decoder)
            val isSoundIdZero = soundId == 0
            return EntitySoundEffect(
                soundId = soundId,
                soundName = if (isSoundIdZero) IdentifierSerializer.deserialize(decoder) else null,
                range = if (isSoundIdZero) decoder.decodeFloat() else null,
                category = SoundCategory.deserialize(decoder),
                entityId = VarIntSerializer.deserialize(decoder),
                volume = decoder.decodeFloat(),
                pitch = decoder.decodeFloat(),
                seed = decoder.decodeLong()
            )
        }

        override fun serialize(encoder: Encoder, value: EntitySoundEffect) {
            VarIntSerializer.serialize(encoder, value.soundId)
            val isSoundIdZero = value.soundId == 0
            if (!isSoundIdZero) IdentifierSerializer.serialize(encoder, value.soundName!!)
            if (!isSoundIdZero) encoder.encodeFloat(value.range!!)
            SoundCategory.serialize(encoder, value.category)
            VarIntSerializer.serialize(encoder, value.entityId)
            encoder.encodeFloat(value.volume)
            encoder.encodeFloat(value.pitch)
            encoder.encodeLong(value.seed)
        }

    }
}

@Serializable(SoundCategory.Serializer::class)
enum class SoundCategory(override val value: Int) : VarIntEnum {
    Master(0),
    Music(1),
    Record(2),
    Weather(3),
    Block(4),
    Hostile(5),
    Neutral(6),
    Player(7),
    Ambient(8),
    Voice(9);

    companion object Serializer : KSerializer<SoundCategory> by varIntEnumSerializer(entries)
}