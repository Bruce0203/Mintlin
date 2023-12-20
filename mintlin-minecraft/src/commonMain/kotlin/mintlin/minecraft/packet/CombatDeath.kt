package mintlin.minecraft.packet

import kotlinx.serialization.Serializable
import mintlin.minecraft.datastructure.Chat
import mintlin.serializer.VarInt

@Serializable
data class CombatDeath(val playerId: VarInt, val message: Chat)