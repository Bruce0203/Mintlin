package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import mintlin.minecraft.datastructure.DoublePosition
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityPositionAccessor : ReadOnlyProperty<Any?, DoublePosition>

interface EntityPositionManipulator : EntityPositionAccessor, ReadWriteProperty<Any?, DoublePosition>

class EntityPositionManipulatorImp : EntityPositionManipulator,
    ReadWriteProperty<Any?, DoublePosition> by Delegates.lateInit()