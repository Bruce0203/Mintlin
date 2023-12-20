package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import mintlin.minecraft.datastructure.FloatRotation
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityRotationAccessor : ReadOnlyProperty<Any?, FloatRotation>

interface EntityRotationManipulator : EntityRotationAccessor, ReadWriteProperty<Any?, FloatRotation>

class EntityRotationManipulatorImp : EntityRotationManipulator,
    ReadWriteProperty<Any?, FloatRotation> by Delegates.lateInit()