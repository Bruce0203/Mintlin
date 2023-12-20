package mintlin.minecraft.packet

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.serializer
import mintlin.minecraft.datastructure.Identifier
import mintlin.minecraft.datastructure.Position
import mintlin.minecraft.datastructure.Slot
import mintlin.serializer.VarInt
import mintlin.serializer.VarIntSerializer
import mintlin.lang.classNameOf

@Serializable(DisplayParticle.Serializer::class)
data class DisplayParticle(
    val particleId: Int,
    val longDistance: Boolean,
    val x: Double, val y: Double, val z: Double,
    val offsetX: Float, val offsetY: Float, val offsetZ: Float,
    val maxSpeed: Float, val particleCount: Int,
    val particle: Particle
) {
    companion object Serializer : KSerializer<DisplayParticle> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<DisplayParticle>())

        override fun deserialize(decoder: Decoder): DisplayParticle {
            val particleId = VarIntSerializer.deserialize(decoder)
            return DisplayParticle(
                particleId = particleId,
                longDistance = decoder.decodeBoolean(),
                x = decoder.decodeDouble(),
                y = decoder.decodeDouble(),
                z = decoder.decodeDouble(),
                offsetX = decoder.decodeFloat(),
                offsetY = decoder.decodeFloat(),
                offsetZ = decoder.decodeFloat(),
                maxSpeed = decoder.decodeFloat(),
                particleCount = decoder.decodeInt(),
                particle = Particle.entries[particleId]?.deserialize(decoder)
                    ?: throw AssertionError("unknown particle id $particleId")
            )
        }

        override fun serialize(encoder: Encoder, value: DisplayParticle) {
            VarIntSerializer.serialize(encoder, value.particleId)
            encoder.encodeBoolean(value.longDistance)
            encoder.encodeDouble(value.x)
            encoder.encodeDouble(value.y)
            encoder.encodeDouble(value.z)
            encoder.encodeFloat(value.offsetX)
            encoder.encodeFloat(value.offsetY)
            encoder.encodeFloat(value.offsetZ)
            encoder.encodeFloat(value.maxSpeed)
            encoder.encodeInt(value.particleCount)
            Particle.entries[value.particleId]?.serialize(encoder, value.particle)
        }

    }

}

@Serializable
sealed interface Particle {
    companion object {
        val entries = listOf(
            serializer<AmbientEntityEffect>(),
            serializer<AngryVillager>(),
            serializer<Block>(),
            serializer<BlockMarker>(),
            serializer<Bubble>(),
            serializer<Cloud>(),
            serializer<Crit>(),
            serializer<DamageIndicator>(),
            serializer<DragonBreath>(),
            serializer<DrippingLava>(),
            serializer<FallingLava>(),
            serializer<LandingLava>(),
            serializer<DrippingWater>(),
            serializer<FallingWater>(),
            serializer<Dust>(),
            serializer<DustColorTransition>(),
            serializer<Effect>(),
            serializer<ElderGuardian>(),
            serializer<EnchantedHit>(),
            serializer<Enchant>(),
            serializer<EndRod>(),
            serializer<EntityEffect>(),
            serializer<ExplosionEmitter>(),
            serializer<Explosion>(),
            serializer<SonicBoom>(),
            serializer<FallingDust>(),
            serializer<Firework>(),
            serializer<Fishing>(),
            serializer<Flame>(),
            serializer<CherryLeaves>(),
            serializer<SculkSoul>(),
            serializer<SculkCharge>(),
            serializer<SculkChargePop>(),
            serializer<SoulFireFlame>(),
            serializer<Soul>(),
            serializer<Flash>(),
            serializer<HappyVillager>(),
            serializer<Composter>(),
            serializer<Heart>(),
            serializer<InstantEffect>(),
            serializer<Item>(),
            serializer<Vibration>(),
            serializer<ItemSlime>(),
            serializer<ItemSnowball>(),
            serializer<LargeSmoke>(),
            serializer<Lava>(),
            serializer<Mycelium>(),
            serializer<Note>(),
            serializer<Poof>(),
            serializer<Portal>(),
            serializer<Rain>(),
            serializer<Smoke>(),
            serializer<Sneeze>(),
            serializer<Spit>(),
            serializer<SquidInk>(),
            serializer<SweepAttack>(),
            serializer<TotemOfUndying>(),
            serializer<Underwater>(),
            serializer<Splash>(),
            serializer<Witch>(),
            serializer<BubblePop>(),
            serializer<CurrentDown>(),
            serializer<BubbleColumnUp>(),
            serializer<Nautilus>(),
            serializer<Dolphin>(),
            serializer<CampfireCosySmoke>(),
            serializer<CampfireSignalSmoke>(),
            serializer<DrippingHoney>(),
            serializer<FallingHoney>(),
            serializer<LandingHoney>(),
            serializer<FallingNectar>(),
            serializer<FallingSporeBlossom>(),
            serializer<Ash>(),
            serializer<CrimsonSpore>(),
            serializer<WarpedSpore>(),
            serializer<SporeBlossomAir>(),
            serializer<DrippingObsidianTear>(),
            serializer<FallingObsidianTear>(),
            serializer<LandingObsidianTear>(),
            serializer<ReversePortal>(),
            serializer<WhiteAsh>(),
            serializer<SmallFlame>(),
            serializer<Snowflake>(),
            serializer<DrippingDripStoneLava>(),
            serializer<FallingDripStoneLava>(),
            serializer<DrippingDripStoneWater>(),
            serializer<FallingDripStoneWater>(),
            serializer<GlowSquidInk>(),
            serializer<Glow>(),
            serializer<WaxOn>(),
            serializer<WaxOff>(),
            serializer<ElectricSpark>(),
            serializer<Scrape>(),
            serializer<Shriek>(),
            serializer<EggCrack>()
        ).run {
            @Suppress("unchecked_cast")
            indices.zip(this).toMap() as Map<Int, KSerializer<Particle>>
        }
    }

    @Serializable
    data object AmbientEntityEffect : Particle

    @Serializable
    data object AngryVillager : Particle

    @Serializable
    data class Block(val blockState: VarInt) : Particle

    @Serializable
    data class BlockMarker(val blockState: VarInt) : Particle

    @Serializable
    data object Bubble : Particle

    @Serializable
    data object Cloud : Particle

    @Serializable
    data object Crit : Particle

    @Serializable
    data object DamageIndicator : Particle

    @Serializable
    data object DragonBreath : Particle

    @Serializable
    data object DrippingLava : Particle

    @Serializable
    data object FallingLava : Particle

    @Serializable
    data object LandingLava : Particle

    @Serializable
    data object DrippingWater : Particle

    @Serializable
    data object FallingWater : Particle

    @Serializable
    data class Dust(
        val red: Float,
        val green: Float,
        val blue: Float,
        val scale: Float
    ) : Particle

    @Serializable
    data class DustColorTransition(
        val fromRed: Float,
        val fromGreen: Float,
        val fromBlue: Float,
        val scale: Float,
        val toRed: Float,
        val toGreen: Float,
        val toBlue: Float
    ) : Particle

    @Serializable
    data object Effect : Particle

    @Serializable
    data object ElderGuardian : Particle

    @Serializable
    data object EnchantedHit : Particle

    @Serializable
    data object Enchant : Particle

    @Serializable
    data object EndRod : Particle

    @Serializable
    data object EntityEffect : Particle

    @Serializable
    data object ExplosionEmitter : Particle

    @Serializable
    data object Explosion : Particle

    @Serializable
    data object SonicBoom : Particle

    @Serializable
    data class FallingDust(
        val blockState: VarInt
    ) : Particle

    @Serializable
    data object Firework : Particle

    @Serializable
    data object Fishing : Particle

    @Serializable
    data object Flame : Particle

    @Serializable
    data object CherryLeaves : Particle

    @Serializable
    data object SculkSoul : Particle

    @Serializable
    data class SculkCharge(val roll: Float) : Particle

    @Serializable
    data object SculkChargePop : Particle

    @Serializable
    data object SoulFireFlame : Particle

    @Serializable
    data object Soul : Particle

    @Serializable
    data object Flash : Particle

    @Serializable
    data object HappyVillager : Particle

    @Serializable
    data object Composter : Particle

    @Serializable
    data object Heart : Particle

    @Serializable
    data object InstantEffect : Particle

    @Serializable
    data class Item(val item: Slot) : Particle

    @Serializable
    data class Vibration(
        val positionSourceType: Identifier,
        val blockPosition: Position,
        override val entityId: VarInt,
        val entityEyeHeight: Float,
        val tick: VarInt
    ) : Particle, IdentifiedEntity

    @Serializable
    data object ItemSlime : Particle

    @Serializable
    data object ItemSnowball : Particle

    @Serializable
    data object LargeSmoke : Particle

    @Serializable
    data object Lava : Particle

    @Serializable
    data object Mycelium : Particle

    @Serializable
    data object Note : Particle

    @Serializable
    data object Poof : Particle

    @Serializable
    data object Portal : Particle

    @Serializable
    data object Rain : Particle

    @Serializable
    data object Smoke : Particle

    @Serializable
    data object Sneeze : Particle

    @Serializable
    data object Spit : Particle

    @Serializable
    data object SquidInk : Particle

    @Serializable
    data object SweepAttack : Particle

    @Serializable
    data object TotemOfUndying : Particle

    @Serializable
    data object Underwater : Particle

    @Serializable
    data object Splash : Particle

    @Serializable
    data object Witch : Particle

    @Serializable
    data object BubblePop : Particle

    @Serializable
    data object CurrentDown : Particle

    @Serializable
    data object BubbleColumnUp : Particle

    @Serializable
    data object Nautilus : Particle

    @Serializable
    data object Dolphin : Particle

    @Serializable
    data object CampfireCosySmoke : Particle

    @Serializable
    data object CampfireSignalSmoke : Particle

    @Serializable
    data object DrippingHoney : Particle

    @Serializable
    data object FallingHoney : Particle

    @Serializable
    data object LandingHoney : Particle

    @Serializable
    data object FallingNectar : Particle

    @Serializable
    data object FallingSporeBlossom : Particle

    @Serializable
    data object Ash : Particle

    @Serializable
    data object CrimsonSpore : Particle

    @Serializable
    data object WarpedSpore : Particle

    @Serializable
    data object SporeBlossomAir : Particle

    @Serializable
    data object DrippingObsidianTear : Particle

    @Serializable
    data object FallingObsidianTear : Particle

    @Serializable
    data object LandingObsidianTear : Particle

    @Serializable
    data object ReversePortal : Particle

    @Serializable
    data object WhiteAsh : Particle

    @Serializable
    data object SmallFlame : Particle

    @Serializable
    data object Snowflake : Particle

    @Serializable
    data object DrippingDripStoneLava : Particle

    @Serializable
    data object FallingDripStoneLava : Particle

    @Serializable
    data object DrippingDripStoneWater : Particle

    @Serializable
    data object FallingDripStoneWater : Particle

    @Serializable
    data object GlowSquidInk : Particle

    @Serializable
    data object Glow : Particle

    @Serializable
    data object WaxOn : Particle

    @Serializable
    data object WaxOff : Particle

    @Serializable
    data object ElectricSpark : Particle

    @Serializable
    data object Scrape : Particle

    @Serializable
    data class Shriek(val delay: VarInt) : Particle

    @Serializable
    data object EggCrack : Particle
}