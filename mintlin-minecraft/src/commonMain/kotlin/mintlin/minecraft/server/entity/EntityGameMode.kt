package mintlin.minecraft.server.entity

import mintlin.lang.lateInit
import mintlin.minecraft.datastructure.GameMode
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityGameModeAccessor : ReadOnlyProperty<Any?, GameMode>

interface EntityGameModeManipulator : ReadWriteProperty<Any?, GameMode>, EntityGameModeAccessor

class EntityGameModeManipulatorImp : EntityGameModeManipulator,
    ReadWriteProperty<Any?, GameMode> by Delegates.lateInit()
