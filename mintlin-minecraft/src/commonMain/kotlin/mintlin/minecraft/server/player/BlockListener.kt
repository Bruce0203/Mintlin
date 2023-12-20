package mintlin.minecraft.server.player

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.BlockBreakEvent
import mintlin.minecraft.server.BlockPlaceEvent
import mintlin.minecraft.server.LightUpdateEvent
import mintlin.minecraft.server.entity.Entity
import mintlin.minecraft.server.entity.EntityGameModeAccessor
import mintlin.minecraft.server.entity.HeldSlotAccessor
import mintlin.minecraft.server.server.Server
import mintlin.lang.Init
import mintlin.minecraft.datastructure.GameMode
import mintlin.minecraft.datastructure.Inventory
import mintlin.minecraft.datastructure.plus
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.network.PacketWriter
import mintlin.minecraft.packet.*
import mintlin.minecraft.registry.Material

class BlockListener(
    private val packetListener: PacketListener,
    private val packetWriter: PacketWriter,
    private val scope: Scope,
    private val inventory: Inventory,
    player: Player, server: Server, entity: Entity,
) : Init(player.listeners {
    val gameMode by scope.get<EntityGameModeAccessor>()
    val heldSlot by scope.get<HeldSlotAccessor>()
    server.onEvent<LightUpdateEvent> {
        packetWriter.sendCachedPacket(it.lightUpdatePacket)
    }
    packetListener.onEvent<PlayerAction> {
        when (it.status) {
            PlayerAction.Status.StartedDigging -> {
                when (gameMode) {
                    GameMode.Survival -> {
                        packetWriter.send(AcknowledgeBlockChange(sequenceId = it.sequenceId))
                    }

                    GameMode.Creative -> server.dispatch(BlockBreakEvent(entity, it.location))
                    GameMode.Adventure -> {}
                    GameMode.Spectator -> {}
                }
            }

            PlayerAction.Status.CancelledDigging -> {}
            PlayerAction.Status.FinishedDigging -> {
                when (gameMode) {
                    GameMode.Creative, GameMode.Survival -> server.dispatch(
                        BlockBreakEvent(
                            entity,
                            it.location
                        )
                    )

                    GameMode.Adventure -> {}
                    GameMode.Spectator -> {}
                }
            }

            PlayerAction.Status.DropItemStack -> {}
            PlayerAction.Status.DropItem -> {}
            PlayerAction.Status.ShootArrowOrFinishEating -> {}
            PlayerAction.Status.SwapItemInHand -> {}
        }
    }
    packetListener.onEvent<UseItemOn> {
        val position = it.location + it.face
        val heldItem = inventory[36 + heldSlot]?.itemId ?: return@onEvent
        val block = Material.item2Block[heldItem]
        if (block == -1) return@onEvent
        server.dispatch(BlockPlaceEvent(entity, position, block))
    }
    server.onEvent<BlockBreakEvent> {
        packetWriter.send(BlockUpdate(location = it.position, blockId = 0))
    }
    server.onEvent<BlockPlaceEvent> {
        packetWriter.send(
            SoundEffect(
                soundIdentifier = SoundIdentifier.Id(1277),
                category = SoundCategory.Block, it.position, volume = 1f, pitch = 0.8f,
                seed = -3489964590574857704
            )
        )
        if (it.entity != entity) {
            packetWriter.send(EntityAnimation(it.entity.id, Animation.SwingMainArm))
        }
        val location = it.position
        packetWriter.send(BlockUpdate(location = location, blockId = it.block))
    }
})