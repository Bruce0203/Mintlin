package mintlin.minecraft.server.entity

import mintlin.datastructure.scope.Scope
import mintlin.datastructure.scope.get
import mintlin.lang.notNull
import mintlin.minecraft.datastructure.DoublePoint3D
import mintlin.minecraft.datastructure.Vector
import mintlin.minecraft.datastructure.normalize
import mintlin.minecraft.datastructure.times
import kotlin.math.min
import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty

interface EntityVelocityAccessor : ReadOnlyProperty<Any?, Vector>

interface EntityVelocityManipulator : EntityVelocityAccessor, ReadWriteProperty<Any?, Vector> {
    fun takeKnockBack(strength: Float, x: Double, z: Double)
}

class EntityVelocityManipulatorImp(scope: Scope) : EntityVelocityManipulator,
    ReadWriteProperty<Any?, Vector> by Delegates.notNull(Vector()) {
    private val tickPerSecond = 20

    private var velocity by this
    private val isOnGround by scope.get<EntityIsOnGroundAccessor>()

    override fun takeKnockBack(strength: Float, x: Double, z: Double) {
        var strength = strength
        if (strength > 0) {
            strength *= tickPerSecond
            val velocityModifier: Vector = DoublePoint3D(x, .0, z).normalize().times(strength)
            val verticalLimit: Double = .4 * tickPerSecond
            velocity = DoublePoint3D(
                velocity.x / 2.0 - velocityModifier.x,
                if (isOnGround) min(verticalLimit, velocity.y / 2.0 + strength) else velocity.y,
                velocity.z / 2.0 - velocityModifier.z
            )
        }
    }

}

