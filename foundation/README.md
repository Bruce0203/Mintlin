# Scoping Injection code example

```kotlin

fun main() {
    Player.createScope(links = Entity.createScope())
}

interface Entity {
    val health: Int
    companion object : ScopeFactoryDSL<Entity, Unit> : scopedDSL({
        single { EntityImp(health = 20) }
    })
}

class EntityImp(override val health: Int) : Entity

interface Player : Entity {
    val session: Session

    companion object : ScopeFactoryDSL<Player, Session> : scopedDSL({
        singleof(::PlayerImp) bind Player::class
    })
}

class PlayerImp(override val session: Session, entity: Entity) : Player, Entity by entity
```