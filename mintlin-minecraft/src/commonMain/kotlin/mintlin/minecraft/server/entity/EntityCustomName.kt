package mintlin.minecraft.server.entity

import mintlin.lang.nullable
import mintlin.minecraft.datastructure.Chat
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

class EntityCustomName : ReadWriteProperty<Any?, Chat?> by Delegates.nullable()
