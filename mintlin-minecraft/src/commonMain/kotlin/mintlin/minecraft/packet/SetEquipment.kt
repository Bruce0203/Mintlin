package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.datastructure.FastArrayList
import mintlin.datastructure.toTypedArray
import mintlin.minecraft.datastructure.Slot
import mintlin.minecraft.datastructure.SlotSerializer
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf

@Serializable(SetEquipment.Serializer::class)
data class SetEquipment(
    override val entityId: Int,
    val equipments: Array<Equipment>
) : IdentifiedEntity {
    companion object Serializer : KSerializer<SetEquipment> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<SetEquipment>())

        override fun deserialize(decoder: Decoder): SetEquipment {
            val entityId = VarIntSerializer.deserialize(decoder)
            val equipments = FastArrayList<Equipment>()
            do {
                val slot = decoder.decodeByte().toInt()
                equipments.add(
                    Equipment(
                        equipment = EquipmentSlot.entries[slot and 0X7F],
                        slot = SlotSerializer.deserialize(decoder)
                    )
                )
            } while (slot and 0x80 == 0x80)
            return SetEquipment(entityId = entityId, equipments = equipments.toTypedArray())
        }

        override fun serialize(encoder: Encoder, value: SetEquipment) {
            VarIntSerializer.serialize(encoder, value.entityId)
            value.equipments.forEachIndexed { index, equipment ->
                encoder.encodeByte((equipment.equipment.ordinal or 0x80).toByte())
                SlotSerializer.serialize(encoder, equipment.slot)
            }
        }
    }
}


data class Equipment(
    val equipment: EquipmentSlot,
    val slot: Slot
)

enum class EquipmentSlot {
    MainHand, OffHand, Boots, Leggings, ChestPlate, Helmet;
}