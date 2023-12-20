package mintlin.minecraft.server.entity

import mintlin.datastructure.eventbus.EventBus
import mintlin.datastructure.eventbus.EventBusImp
import mintlin.datastructure.scope.*
import mintlin.minecraft.datastructure.*
import mintlin.serializer.UUID

interface Entity : Scope, EntityFacade, EventBus {
    companion object : ScopeFactoryDSL<Entity, Unit> by scopedDSL({
        singleOf(::EntityImp) bind Entity::class
        single { Metadata() }

        singleOf(::EntityTypeManipulatorImp) binds arrayOf(
            EntityTypeManipulator::class,
            EntityTypeAccessor::class
        )

        singleOf(::EntityIDManipulatorImp) binds arrayOf(
            EntityIDManipulator::class,
            EntityIDAccessor::class
        )

        singleOf(::EntityUUIDManipulatorImp) binds arrayOf(
            EntityUUIDManipulator::class,
            EntityUUIDAccessor::class
        )

        singleOf(::EntityCustomName)

        singleOf(::EntityGameModeManipulatorImp) binds arrayOf(
            EntityGameModeManipulator::class,
            EntityGameModeAccessor::class
        )

        singleOf(::HeldSlotManipulatorImp) binds arrayOf(
            HeldSlotAccessor::class,
            HeldSlotManipulator::class
        )

        singleOf(::EntityPositionManipulatorImp) binds arrayOf(
            EntityPositionManipulator::class,
            EntityPositionAccessor::class
        )

        singleOf(::EntityRotationManipulatorImp) binds arrayOf(
            EntityRotationManipulator::class,
            EntityRotationAccessor::class
        )

        singleOf(::EntityIsOnGroundManipulatorImp) binds arrayOf(
            EntityIsOnGroundManipulator::class,
            EntityIsOnGroundAccessor::class
        )

        singleOf(::EntityLocationManipulatorImp) binds arrayOf(
            EntityLocationManipulator::class,
            EntityLocationAccessor::class
        )

        singleOf(::EntityVelocityManipulatorImp) binds arrayOf(
            EntityVelocityManipulator::class,
            EntityVelocityAccessor::class
        )

        singleOf(::InventoryImp) bind Inventory::class
    })
}

interface EntityFacade {
    var uuid: UUID
    var id: Int
    var gameMode: GameMode
    var customName: Chat?
    var type: EntityType
    var pos: DoublePosition
    var rot: FloatRotation
    var isOnGround: Boolean
    var velocity: Vector
    val entityVelocityManipulator: EntityVelocityManipulator

}

class EntityImp(scope: Scope) : Entity, EntityFacade,
    EventBus by EventBusImp(scope), Scope by scope {
    override var uuid by scope.get<EntityUUIDManipulator>()
    override var id by scope.get<EntityIDManipulator>()
    override var gameMode by scope.get<EntityGameModeManipulator>()
    override var customName by scope.get<EntityCustomName>()
    override var type by scope.get<EntityTypeManipulator>()
    override var pos by scope.get<EntityPositionManipulator>()
    override var rot by scope.get<EntityRotationManipulator>()
    override var isOnGround by scope.get<EntityIsOnGroundManipulator>()
    override var velocity by scope.get<EntityVelocityManipulator>()
    override val entityVelocityManipulator = scope.get<EntityVelocityManipulator>()
}
