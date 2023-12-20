@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.datastructure.level

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.BitSet
import mintlin.serializer.VarIntSizedByteArraySerializer
import mintlin.lang.classNameOf
import mintlin.serializer.varIntSizedArraySerializer

@Serializable
data class Light(
    var skyMask: BitSet = BitSet(),
    var blockMask: BitSet = BitSet(),
    var emptySkyMask: BitSet = BitSet(),
    var emptyBlockMask: BitSet = BitSet(),
    @Serializable(ByteArraysSerializer::class)
    var skyLights: Array<ByteArray> = arrayOf(),
    @Serializable(ByteArraysSerializer::class)
    var blockLights: Array<ByteArray> = arrayOf()
) {
    override fun toString() = "${classNameOf<Light>()}(...)"
}

object ByteArraysSerializer :
    KSerializer<Array<ByteArray>> by varIntSizedArraySerializer(VarIntSizedByteArraySerializer)

