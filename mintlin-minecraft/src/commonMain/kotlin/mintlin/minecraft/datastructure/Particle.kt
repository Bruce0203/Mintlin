@file:Suppress("SpellCheckingInspection")

package mintlin.minecraft.datastructure

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import mintlin.serializer.VarInt
import mintlin.lang.classNameOf

@Serializable
sealed interface Particle {
    val name: String
    val id: Int

    open class ParticleImp internal constructor(override val name: String, override val id: Int) : Particle

    data object AmbientEntityEffect : Particle by ParticleImp("minecraft:ambient_entity_effect", 0)
    data object AngryVillager : Particle by ParticleImp("minecraft:angry_villager", 1)
    data class Block(val blockState: Int) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:block", 2)
    }

    data class BlockMarker(val blockState: Int) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:block_marker", 3)
    }

    data object Bubble : Particle by ParticleImp("minecraft:bubble", 4)
    data object Cloud : Particle by ParticleImp("minecraft:cloud", 5)
    data object Crit : Particle by ParticleImp("minecraft:crit", 6)
    data object DamageIndicator : Particle by ParticleImp("minecraft:damage_indicator", 7)
    data object DragonBreath : Particle by ParticleImp("minecraft:dragon_breath", 8)
    data object DrippingLava : Particle by ParticleImp("minecraft:dripping_lava", 9)
    data object FallingLava : Particle by ParticleImp("minecraft:falling_lava", 10)
    data object LandingLava : Particle by ParticleImp("minecraft:landing_lava", 11)
    data object DrippingWater : Particle by ParticleImp("minecraft:dripping_water", 12)
    data object FallingWater : Particle by ParticleImp("minecraft:falling_water", 13)
    data class Dust(val red: Float, val green: Float, val blue: Float, val scale: Float) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:dust", 14)
    }

    data class DustColorTransition(
        val fromRed: Float, val fromGreen: Float, val fromBlue: Float, val scale: Float,
        val toRed: Float, val toGreen: Float, val toBlue: Float
    ) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:dust_color_transition", 15)
    }

    data object Effect : Particle by ParticleImp("minecraft:effect", 16)
    data object ElderGuardian : Particle by ParticleImp("minecraft:elder_guardian", 17)
    data object EnchantedHit : Particle by ParticleImp("minecraft:enchanted_hit", 18)
    data object Enchant : Particle by ParticleImp("minecraft:enchant", 19)
    data object EndRod : Particle by ParticleImp("minecraft:end_rod", 20)
    data object EntityEffect : Particle by ParticleImp("minecraft:entity_effect", 21)
    data object ExplosionEmitter : Particle by ParticleImp("minecraft:explosion_emitter", 22)
    data object Explosion : Particle by ParticleImp("minecraft:explosion", 23)
    data object Gust : Particle by ParticleImp("minecraft:gust", 24)
    data object GustEmitter : Particle by ParticleImp("minecraft:gust_emitter", 25)
    data object SonicBoom : Particle by ParticleImp("minecraft:sonic_boom", 26)
    data class FallingDust(val blockState: Int) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:falling_dust", 27)
    }

    data object Firework : Particle by ParticleImp("minecraft:firework", 28)
    data object Fishing : Particle by ParticleImp("minecraft:fishing", 29)
    data object Flame : Particle by ParticleImp("minecraft:flame", 30)
    data object CherryLeaves : Particle by ParticleImp("minecraft:cherry_leaves", 31)
    data object SculkSoul : Particle by ParticleImp("minecraft:sculk_soul", 32)
    data class SculkCharge(val roll: Float) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:sculk_charge", 33)
    }

    data object SculkChargePop : Particle by ParticleImp("minecraft:sculk_charge_pop", 34)
    data object SoulFireFlame : Particle by ParticleImp("minecraft:soul_fire_flame", 35)
    data object Soul : Particle by ParticleImp("minecraft:soul", 36)
    data object Flash : Particle by ParticleImp("minecraft:flash", 37)
    data object HappyVillager : Particle by ParticleImp("minecraft:happy_villager", 38)
    data object Composter : Particle by ParticleImp("minecraft:composter", 39)
    data object Heart : Particle by ParticleImp("minecraft:heart", 40)
    data object InstantEffect : Particle by ParticleImp("minecraft:instant_effect", 41)
    data class Item(val item: Slot) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:item", 42)
    }

    data class Vibration(
        val positionSource: Identifier,
        val blockPosition: Position,
        val entityId: VarInt,
        val entityEyeHeight: Float,
        val ticks: VarInt
    ) : Particle by this {
        companion object : Particle by ParticleImp("minecraft:vibration", 43)
    }

    data object ItemSlime : Particle by ParticleImp("minecraft:item_slime", 44)
    data object ItemSnowball : Particle by ParticleImp("minecraft:item_snowball", 45)
    data object LargeSmoke : Particle by ParticleImp("minecraft:large_smoke", 46)
    data object Lava : Particle by ParticleImp("minecraft:lava", 47)
    data object Mycelium : Particle by ParticleImp("minecraft:mycelium", 48)
    data object Note : Particle by ParticleImp("minecraft:note", 49)
    data object Poof : Particle by ParticleImp("minecraft:poof", 50)
    data object Portal : Particle by ParticleImp("minecraft:portal", 51)
    data object Rain : Particle by ParticleImp("minecraft:rain", 52)
    data object Smoke : Particle by ParticleImp("minecraft:smoke", 53)
    data object WhiteSmoke : Particle by ParticleImp("minecraft:white_smoke", 54)
    data object Sneeze : Particle by ParticleImp("minecraft:sneeze", 55)
    data object Spit : Particle by ParticleImp("minecraft:spit", 56)
    data object SquidInk : Particle by ParticleImp("minecraft:squid_ink", 57)
    data object SweepAttack : Particle by ParticleImp("minecraft:sweep_attack", 58)
    data object TotemOfUndying : Particle by ParticleImp("minecraft:totem_of_undying", 59)
    data object UnderWater : Particle by ParticleImp("minecraft:underwater", 60)
    data object Splash : Particle by ParticleImp("minecraft:splash", 61)
    data object Witch : Particle by ParticleImp("minecraft:witch", 62)
    data object BubblePop : Particle by ParticleImp("minecraft:bubble_pop", 63)
    data object CurrentDown : Particle by ParticleImp("minecraft:current_down", 64)
    data object BubbleColumnUp : Particle by ParticleImp("minecraft:bubble_column_up", 65)
    data object Nautilus : Particle by ParticleImp("minecraft:nautilus", 66)
    data object Dolphin : Particle by ParticleImp("minecraft:dolphin", 67)
    data object CampfireCosySmoke : Particle by ParticleImp("minecraft:campfire_cosy_smoke", 68)
    data object CampfireSignalSmoke : Particle by ParticleImp("minecraft:campfire_signal_smoke", 69)
    data object DrippingHoney : Particle by ParticleImp("minecraft:dripping_honey", 70)
    data object FallingHoney : Particle by ParticleImp("minecraft:falling_honey", 71)
    data object LandingHoney : Particle by ParticleImp("minecraft:landing_honey", 72)
    data object FallingNectar : Particle by ParticleImp("minecraft:falling_nectar", 73)
    data object FallingSporeBlossom : Particle by ParticleImp("minecraft:falling_spore_blossom", 74)
    data object Ash : Particle by ParticleImp("minecraft:ash", 75)
    data object CrimsonSpore : Particle by ParticleImp("minecraft:crimson_spore", 76)
    data object WarpedSpore : Particle by ParticleImp("minecraft:warped_spore", 77)
    data object SporeBlossomAir : Particle by ParticleImp("minecraft:spore_blossom_air", 78)
    data object DrippingObsidianTear : Particle by ParticleImp("minecraft:dripping_obsidian_tear", 79)
    data object FallingObsidianTear : Particle by ParticleImp("minecraft:falling_obsidian_tear", 80)
    data object LandingObsidianTear : Particle by ParticleImp("minecraft:landing_obsidian_tear", 81)
    data object ReversePortal : Particle by ParticleImp("minecraft:reverse_portal", 82)
    data object WhiteAsh : Particle by ParticleImp("minecraft:white_ash", 83)
    data object SmallFlame : Particle by ParticleImp("minecraft:small_flame", 84)
    data object Snowflake : Particle by ParticleImp("minecraft:snowflake", 85)
    data object DrippingDripstoneLava : Particle by ParticleImp("minecraft:dripping_dripstone_lava", 86)
    data object FallingDripstoneLava : Particle by ParticleImp("minecraft:falling_dripstone_lava", 87)
    data object DrippingDripstoneWater : Particle by ParticleImp("minecraft:dripping_dripstone_water", 88)
    data object FallingDripstoneWater : Particle by ParticleImp("minecraft:falling_dripstone_water", 89)
    data object GlowSquidInk : Particle by ParticleImp("minecraft:glow_squid_ink", 90)
    data object Glow : Particle by ParticleImp("minecraft:glow", 91)
    data object WaxOn : Particle by ParticleImp("minecraft:wax_on", 92)
    data object WaxOff : Particle by ParticleImp("minecraft:wax_off", 93)
    data object ElectricSpark : Particle by ParticleImp("minecraft:electric_spark", 94)
    data object Scrape : Particle by ParticleImp("minecraft:scrape", 95)
    data class Shriek(val delay: VarInt) {
        companion object : Particle by ParticleImp("minecraft:shriek", 96)
    }

    data object EggCrack : Particle by ParticleImp("minecraft:egg_crack", 97)
    data object DustPlume : Particle by ParticleImp("minecraft:dust_plume", 98)
    data object GustDust : Particle by ParticleImp("minecraft:gust_dust", 99)
    data object TrialSpawnerDetection : Particle by ParticleImp("minecraft:trial_spawner_detection", 100)

    companion object {
        val entries = arrayOf(
            AmbientEntityEffect,
            AngryVillager,
            Block,
            BlockMarker,
            Bubble,
            Cloud,
            Crit,
            DamageIndicator,
            DragonBreath,
            DrippingLava,
            FallingLava,
            LandingLava,
            DrippingWater,
            FallingWater,
            Dust,
            DustColorTransition,
            Effect,
            ElderGuardian,
            EnchantedHit,
            Enchant,
            EndRod,
            EntityEffect,
            ExplosionEmitter,
            Explosion,
            Gust,
            GustEmitter,
            SonicBoom,
            FallingDust,
            Firework,
            Fishing,
            Flame,
            CherryLeaves,
            SculkSoul,
            SculkCharge,
            SculkChargePop,
            SoulFireFlame,
            Soul,
            Flash,
            HappyVillager,
            Composter,
            Heart,
            InstantEffect,
            Item,
            Vibration,
            ItemSlime,
            ItemSnowball,
            LargeSmoke,
            Lava,
            Mycelium,
            Note,
            Poof,
            Portal,
            Rain,
            Smoke,
            WhiteSmoke,
            Sneeze,
            Spit,
            SquidInk,
            SweepAttack,
            TotemOfUndying,
            UnderWater,
            Splash,
            Witch,
            BubblePop,
            CurrentDown,
            BubbleColumnUp,
            Nautilus,
            Dolphin,
            CampfireCosySmoke,
            CampfireSignalSmoke,
            DrippingHoney,
            FallingHoney,
            LandingHoney,
            FallingNectar,
            FallingSporeBlossom,
            Ash,
            CrimsonSpore,
            WarpedSpore,
            SporeBlossomAir,
            DrippingObsidianTear,
            FallingObsidianTear,
            LandingObsidianTear,
            ReversePortal,
            WhiteAsh,
            SmallFlame,
            Snowflake,
            DrippingDripstoneLava,
            FallingDripstoneLava,
            DrippingDripstoneWater,
            FallingDripstoneWater,
            GlowSquidInk,
            Glow,
            WaxOn,
            WaxOff,
            ElectricSpark,
            Scrape,
            Shriek,
            EggCrack,
            DustPlume,
            GustDust,
            TrialSpawnerDetection
        )
    }
}

object ParticleSerializer : KSerializer<Particle> {
    override val descriptor = buildClassSerialDescriptor(classNameOf<Particle>())

    override fun deserialize(decoder: Decoder): Particle {
        TODO()
    }

    override fun serialize(encoder: Encoder, value: Particle) {
        TODO()
    }

}