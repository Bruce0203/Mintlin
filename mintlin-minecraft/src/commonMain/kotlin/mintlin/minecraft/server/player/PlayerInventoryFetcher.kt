package mintlin.minecraft.server.player

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.minecraft.server.entity.HeldSlotManipulator
import mintlin.lang.Init
import mintlin.minecraft.datastructure.Inventory
import mintlin.minecraft.network.PacketListener
import mintlin.minecraft.packet.ServerBoundSetHeldItem
import mintlin.minecraft.packet.SetCreativeModeSlot

class PlayerInventoryFetcher(
    packetListener: PacketListener,
    inventory: Inventory,
    player: Player,
    scope: Scope
) : Init(player.listeners {
    var heldSlot by scope.get<HeldSlotManipulator>()
    packetListener.onEvent<SetCreativeModeSlot> {
        inventory[it.slot] = it.clickedItem.item
    }
    packetListener.onEvent<ServerBoundSetHeldItem> {
        heldSlot = it.slot
    }
})