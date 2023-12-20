package mintlin.minecraft.server.entity

import mintlin.lang.notNull
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface HeldSlotAccessor : ReadOnlyProperty<Any?, Int>

interface HeldSlotManipulator : HeldSlotAccessor, ReadWriteProperty<Any?, Int>

class HeldSlotManipulatorImp : HeldSlotManipulator, ReadWriteProperty<Any?, Int> by Delegates.notNull(0)
