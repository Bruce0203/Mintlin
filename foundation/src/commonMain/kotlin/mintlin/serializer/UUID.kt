package mintlin.serializer

import kotlinx.serialization.Serializable
import kotlin.random.Random

@Suppress("EXPERIMENTAL_API_USAGE")
@Serializable(UUIDSerializer::class)
class UUID(val data: UByteArrayInt) {
    override fun equals(other: Any?): Boolean = other is UUID && this.data.bytes.contentEquals(other.data.bytes)
    override fun hashCode(): Int = this.data.bytes.contentHashCode()

	companion object {
		private val regex =
			Regex("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}", RegexOption.IGNORE_CASE)

        val NIL: UUID get() = UUID("00000000-0000-0000-0000-000000000000")

		private fun fix(data: UByteArrayInt, version: Int, variant: Int): UByteArrayInt {
			data[6] = ((data[6] and 0b0000_1111) or (version shl 4))
			data[8] = ((data[8] and 0x00_111111) or (variant shl 6))
			return data
		}

		fun randomUUID(random: Random = Random): UUID = UUID(fix(UByteArrayInt(16).apply {
			random.nextBytes(this.asByteArray())
		}, version = 4, variant = 1))

		operator fun invoke(str: String): UUID {
			if (regex.matchEntire(str) == null) throw IllegalArgumentException("Invalid UUID")
			return UUID(Hex.decode(str.replace("-", "")).asUByteArrayInt())
		}
	}

	val version: Int get() = (data[6] ushr 4) and 0b1111
	val variant: Int get() = (data[8] ushr 6) and 0b11

	fun toString(includeBrackets: Boolean): String = buildString(36) {
        for (n in 0 until 16) {
            val c = data[n]
            append(Hex.encodeCharLower(c ushr 4))
            append(Hex.encodeCharLower(c and 0xF))
            if ((n == 3 || n == 5 || n == 7 || n == 9) && includeBrackets) append('-')
        }
    }

	override fun toString(): String = toString(includeBrackets = true)
}
