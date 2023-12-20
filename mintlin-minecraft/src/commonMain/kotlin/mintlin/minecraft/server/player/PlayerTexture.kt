package mintlin.minecraft.server.player

import mintlin.lang.nullable
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty

class PlayerTexture : ReadWriteProperty<Any?, String?> by Delegates.nullable()