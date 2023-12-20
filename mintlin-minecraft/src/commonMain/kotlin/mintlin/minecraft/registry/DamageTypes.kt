@file:Suppress("EnumEntryName")

package mintlin.minecraft.registry

import mintlin.minecraft.packet.DamageType
import mintlin.minecraft.packet.Entry
import mintlin.minecraft.packet.Registry

enum class DamageTypes(
    val messageId: String, val scaling: String, val exhaustion: Float,
    val deathMessageType: String? = null, val effects: String? = null
) {
    arrow(messageId = "arrow", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    bad_respawn_point(
        messageId = "badRespawnPoint",
        scaling = "always",
        exhaustion = 0.1f,
        deathMessageType = "intentional_game_design"
    ),
    cactus(messageId = "cactus", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    cramming(messageId = "cramming", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    dragon_breath(messageId = "dragonBreath", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    drown(messageId = "drown", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f, effects = "drowning"),

    dry_out(messageId = "dryout", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    explosion(messageId = "explosion", scaling = "always", exhaustion = 0.1f),
    fall(
        messageId = "fall",
        scaling = "when_caused_by_living_non_player",
        exhaustion = 0.0f,
        deathMessageType = "fall_variants"
    ),
    falling_anvil(messageId = "anvil", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    falling_block(messageId = "fallingBlock", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    falling_stalactite(
        messageId = "fallingStalactite",
        scaling = "when_caused_by_living_non_player",
        exhaustion = 0.1f
    ),
    fireball(
        messageId = "fireball",
        scaling = "when_caused_by_living_non_player",
        exhaustion = 0.1f,
        effects = "burning"
    ),

    fireworks(messageId = "fireworks", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    fly_into_wall(messageId = "flyIntoWall", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    freeze(messageId = "freeze", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f, effects = "freezing"),

    generic(messageId = "generic", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    generic_kill(messageId = "genericKill", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    hot_floor(
        messageId = "hotFloor",
        scaling = "when_caused_by_living_non_player",
        exhaustion = 0.1f,
        effects = "burning"
    ),

    in_fire(messageId = "inFire", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f, effects = "burning"),

    in_wall(messageId = "inWall", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    indirect_magic(messageId = "indirectMagic", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    lava(messageId = "lava", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f, effects = "burning"),

    lightning_bolt(messageId = "lightningBolt", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    magic(messageId = "magic", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    mob_attack(messageId = "mob", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    mob_attack_no_aggro(messageId = "mob", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    mob_projectile(messageId = "mob", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    on_fire(messageId = "onFire", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f, effects = "burning"),

    out_of_world(messageId = "outOfWorld", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    outside_border(messageId = "outsideBorder", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    player_attack(messageId = "player", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    player_explosion(messageId = "explosion.player", scaling = "always", exhaustion = 0.1f),
    sonic_boom(messageId = "sonic_boom", scaling = "always", exhaustion = 0.0f),
    stalagmite(messageId = "stalagmite", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    starve(messageId = "starve", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    sting(messageId = "sting", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    sweet_berry_bush(
        messageId = "sweetBerryBush",
        scaling = "when_caused_by_living_non_player",
        exhaustion = 0.1f,
        effects = "poking"
    ),

    thorns(messageId = "thorns", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f, effects = "thorns"),

    thrown(messageId = "thrown", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    trident(messageId = "trident", scaling = "when_caused_by_living_non_player", exhaustion = 0.1f),
    unattributed_fireball(
        messageId = "onFire",
        scaling = "when_caused_by_living_non_player",
        exhaustion = 0.01f,
        effects = "burning"
    ),

    wither(messageId = "wither", scaling = "when_caused_by_living_non_player", exhaustion = 0.0f),
    wither_skull(messageId = "witherSkull", scaling = "when_caused_by_living_non_player", 0.01f);

    val entry = Entry(
        "minecraft:$name", ordinal, DamageType(
            messageId = messageId, scaling = scaling, exhaustion = exhaustion,
            deathMessageType = deathMessageType, effects = effects
        )
    )

    companion object {
        val registry = Registry(
            type = "minecraft:damage_type", value = entries.map { it.entry }.toTypedArray()
        )
    }
}
