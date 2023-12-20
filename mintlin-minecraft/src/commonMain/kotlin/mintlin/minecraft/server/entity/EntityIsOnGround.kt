package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityIsOnGroundAccessor : ReadOnlyProperty<Any?, Boolean>

interface EntityIsOnGroundManipulator : EntityIsOnGroundAccessor, ReadWriteProperty<Any?, Boolean>

class EntityIsOnGroundManipulatorImp : EntityIsOnGroundManipulator,
    ReadWriteProperty<Any?, Boolean> by Delegates.lateInit()
