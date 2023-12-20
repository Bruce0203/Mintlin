package mintlin.format

import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder

interface PolymorphicDecoder : CompositeDecoder, Decoder {
    fun decodeValue(): Any
}
