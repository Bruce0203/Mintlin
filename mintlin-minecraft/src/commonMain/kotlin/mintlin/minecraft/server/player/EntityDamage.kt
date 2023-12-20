package mintlin.minecraft.server.player

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.EntityAttackEvent
import mintlin.minecraft.server.EntityInteractEvent
import mintlin.minecraft.server.EntitySwingArmEvent
import mintlin.minecraft.server.EntityVelocityEvent
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.entity.metadata.LivingEntityMetadata
import mintlin.minecraft.server.entity.metadata.health
import mintlin.minecraft.server.server.Server
import mintlin.minecraft.server.server.list.EntityList
import mintlin.io.network.ServerTickRateDelegate
import mintlin.lang.Init
import mintlin.minecraft.datastructure.*
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import kotlin.math.cos
import kotlin.math.sin

class EntityDamage(
    packetListener: PacketListener,
    packetWriter: PacketWriter,
    scope: Scope,
    server: Server, entity: Entity, entities: EntityList,
    player: Player
) : Init(player.listeners {
    val tickRate by scope.get<ServerTickRateDelegate>()
    packetListener.onEvent<Interact> { interact ->
        val isSneaking = interact.isSneaking
        val target = entities.find { e -> e.id == interact.entityId }!!
        server.dispatch(EntityInteractEvent(entity, target, interact.interact, isSneaking))
        if (interact.interact is Interaction.Attack) {
            val yaw = entity.rot.yaw
            val strength = 0.4f
            target.entityVelocityManipulator.takeKnockBack(strength, sin(yaw * 0.017453292), -cos(yaw * 0.017453292))
            server.dispatch(EntityAttackEvent(entity, target, isSneaking))
            server.dispatch(EntityVelocityEvent(target, strength, target.velocity))
        }
    }
    packetListener.onEvent<SwingArm> {
        server.dispatch(EntitySwingArmEvent(entity, it.swingHand))
    }
    server.onEvent<EntitySwingArmEvent> {
        val swungEntity = it.entity
        if (entity == swungEntity) return@onEvent
        packetWriter.send(
            EntityAnimation(
                swungEntity.id, when (it.hand) {
                    Hand.MainHand -> Animation.SwingMainArm
                    Hand.OffHand -> Animation.SwingOffHand
                }
            )
        )
    }
    server.onEvent<EntityVelocityEvent> {
        packetWriter.send(
            SetEntityVelocity(
                it.entity.id,
                velocity = (it.velocity * (8000f / tickRate.inWholeSeconds)).toShort()
            )
        )
    }
    server.onEvent<EntityInteractEvent> {
        when (it.target.gameMode) {
            GameMode.Survival, GameMode.Adventure, GameMode.Creative -> {
                when (it.interaction) {
                    is Interaction.Attack -> {
                        packetWriter.send(
                            DamageEvent(
                                it.target.id,
                                sourceTypeId = 31, sourceCauseId = 84, sourceDirectId = 84, hasSourceRotation = null
                            )
                        )
                        packetWriter.send(HurtAnimation(it.target.id, yaw = 1.8908088f))
                        packetWriter.send(SetEntityMetadata(it.target.id, Metadata().apply {
                            LivingEntityMetadata(this).health = 18.074665F
                        }))
                        if (it.target == entity) packetWriter.send(
                            SetHealth(
                                health = 18.074665F, food = 20, foodSaturation = 0f
                            )
                        )
                    }

                    is Interaction.Interact -> {}
                    is Interaction.InteractAt -> {}
                }
            }

            GameMode.Creative, GameMode.Spectator -> {}
        }
    }
})
