package mintlin.minecraft.server.player

import mintlin.minecraft.datastructure.Attribute
import mintlin.minecraft.datastructure.AttributeKey

class PlayerAttributes {
    private val map: MutableMap<AttributeKey, Attribute> = mutableMapOf()

    operator fun get(key: AttributeKey) = map.computeIfAbsent(key) { Attribute(key, key.default) }
}