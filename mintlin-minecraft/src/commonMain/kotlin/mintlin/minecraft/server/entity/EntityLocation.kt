package mintlin.minecraft.server.entity

import mintlin.minecraft.datastructure.Location
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

interface EntityLocationAccessor : ReadOnlyProperty<Any?, Location>

interface EntityLocationManipulator : EntityLocationAccessor, ReadWriteProperty<Any?, Location>

class EntityLocationManipulatorImp(
    entityRotationManipulator: EntityRotationManipulator,
    entityPositionManipulator: EntityPositionManipulator
) : EntityLocationManipulator {
    private var rotation by entityRotationManipulator
    private var position by entityPositionManipulator

    override fun getValue(thisRef: Any?, property: KProperty<*>) = Location(position, rotation)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Location) {
        position = value.position
        rotation = value.rotation
    }

}