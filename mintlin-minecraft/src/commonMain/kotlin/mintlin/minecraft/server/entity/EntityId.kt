package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityIDAccessor : ReadOnlyProperty<Any?, Int>

interface EntityIDManipulator : ReadWriteProperty<Any?, Int>, EntityIDAccessor

class EntityIDManipulatorImp : EntityIDManipulator, ReadWriteProperty<Any?, Int> by Delegates.lateInit()

