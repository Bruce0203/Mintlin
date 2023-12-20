package mintlin.minecraft.server.server

import mintlin.lang.notNull
import mintlin.minecraft.datastructure.StringComponent
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface MessageOfTheDayAccessor : ReadOnlyProperty<Any?, StringComponent>

interface MessageOfTheDayManipulator : MessageOfTheDayAccessor, ReadWriteProperty<Any?, StringComponent>

class MessageOfTheDayManipulatorImp : MessageOfTheDayManipulator,
    ReadWriteProperty<Any?, StringComponent> by Delegates.notNull(StringComponent("A Minecraft Server"))