package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import mintlin.minecraft.datastructure.EntityType
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityTypeAccessor : ReadOnlyProperty<Any?, EntityType>

interface EntityTypeManipulator : EntityTypeAccessor, ReadWriteProperty<Any?, EntityType>

class EntityTypeManipulatorImp : EntityTypeManipulator, ReadWriteProperty<Any?, EntityType> by Delegates.lateInit()