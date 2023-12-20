package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import mintlin.serializer.UUID
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityUUIDAccessor : ReadOnlyProperty<Any?, UUID>

interface EntityUUIDManipulator : ReadWriteProperty<Any?, UUID>, EntityUUIDAccessor

class EntityUUIDManipulatorImp : EntityUUIDManipulator, ReadWriteProperty<Any?, UUID> by Delegates.lateInit()
