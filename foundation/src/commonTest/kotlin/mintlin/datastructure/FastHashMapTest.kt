package mintlin.datastructure

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe

class FastHashMapTest: StringSpec({
    lateinit var map: FastMap<String, String>

    val keyModels = Array(100) {
        "$it"
    }

    val valueModels = Array(100) {
        "${it + 100}"
    }

    beforeEach {
        map = FastIdentityMap()
    }

    "add 100, than size should be 100" {
        repeat(100) { map.put(keyModels[it], valueModels[it]) }

        map.forEach { entry ->
            entry.key shouldBe keyModels[entry.key.toInt()]
        }
    }

    "add 100 and remove 100 should be empty" {
        repeat(100) { map.put(keyModels[it], valueModels[it]) }
        repeat(100) { map.remove(keyModels[it]) }
        map.size shouldBe 0
    }

    "remove on empty list should return false" {
        repeat(100) {
            map.clear()
            map.remove(keyModels[it]) shouldBe false
        }
    }

    "add same instance should be ok" {
        repeat(100) { value ->
            repeat(100) {
                shouldNotThrowAny { map.put(keyModels[value], valueModels[value]) }
            }
        }
        map.size shouldBe 100*100
    }

    "not supported that not have unique hashcode implementation" {
        val list = FastArrayList<String>()
        val hadException = runCatching {
            repeat(100) { list.add("$it") }
            repeat(100) { list.remove("$it") }
        }.isFailure
        val isRemovalNotWorked = list.size == 100
        (hadException or isRemovalNotWorked) shouldBe true
    }

    "add after remove" {
        repeat(4) {
            repeat(100) { map.put(keyModels[it], valueModels[it]) }
            repeat(100) { map.remove(keyModels[it]) }
        }
        map.size shouldBe 0
    }
})