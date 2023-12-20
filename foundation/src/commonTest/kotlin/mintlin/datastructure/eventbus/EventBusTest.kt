package mintlin.datastructure.eventbus

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mintlin.datastructure.scope.ScopeImp


class EventBusTest : StringSpec({
    lateinit var eventBus: EventBus

    lateinit var anotherEventBus: EventBus

    class Event
    class AnotherEvent
    lateinit var event: Event
    lateinit var anotherEvent: AnotherEvent

    val sampleScope = ScopeImp()

    fun reset() {
        eventBus = EventBusImp(sampleScope)
        anotherEventBus = EventBusImp(sampleScope)
        event = Event()
        anotherEvent = AnotherEvent()
    }

    beforeEach { reset() }

    "open listeners registrar, but not registered anything should be ok" {
        repeat(100) { shouldNotThrowAny { eventBus.listeners { } } }
    }

    "dispatch 100 times on a single listener added eventbus" {
        var executedTime = 0
        eventBus.listeners {
            anotherEventBus.onEvent<Event> { executedTime++ }
        }
        repeat(100) {
            anotherEventBus.dispatch(event)
        }
        executedTime shouldBe 100
    }

    "dispatch 100 times on a multiple listeners on a same event" {
        var executedTime = 0
        eventBus.listeners {
            repeat(100) { anotherEventBus.onEvent<Event> { executedTime++ } }
        }
        repeat(100) {
            anotherEventBus.dispatch(event)
        }
        executedTime shouldBe(100 * 100)
    }

    "dispatched event instance should be same as listener receiving event" {
        eventBus.listeners {
            repeat(100) { anotherEventBus.onEvent<AnotherEvent> { it shouldBe anotherEvent } }
            repeat(100) { anotherEventBus.onEvent<Event> { it shouldBe event } }
        }
        eventBus.listeners {
        }
        repeat(100) {
            anotherEventBus.dispatch(anotherEvent)
            anotherEventBus.dispatch(event)
        }
        repeat(100) {
            anotherEventBus.dispatch(anotherEvent)
            anotherEventBus.dispatch(anotherEvent)
        }
        repeat(100) {
            anotherEventBus.dispatch(event)
            anotherEventBus.dispatch(event)
        }
    }

    "cancellation" {
        var isExecuted = false
        eventBus.listeners {
            anotherEventBus.onEvent<Event> { isExecuted = true }
            anotherEventBus.onEvent<Event>(Priority.LOW) { cancelEvent() }
        }
        repeat(100) { anotherEventBus.dispatch(event) }
        isExecuted shouldBe false
    }

//    "listener closed on scope closed" {
//        var isExecuted = false
//        eventBus.listeners {
//            anotherEventBus.onEvent<Event> { isExecuted = true }
//        }
//        anotherEventBus.closeAllListeners()
//        anotherEventBus.dispatch(event)
//    }
})
