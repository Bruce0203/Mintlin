package mintlin.datastructure

import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import mintlin.lang.identityHashCode

class FastArrayListTest: StringSpec({
    lateinit var list: FastArrayList<String>

    val models = Array(100) {
        "$it"
    }

    beforeEach {
        list = FastArrayList()
    }

    "add 100, than size should be 100" {
        repeat(100) { list.add(models[it]) }

        list.forEachIndexed { index, element ->
            element shouldBe models[index]
        }
    }

    "add 100 and remove 100 should be empty" {
        repeat(100) { list.add(models[it]) }
        repeat(100) { list.remove(models[it]) }
        list.size shouldBe 0
    }

    "remove on empty list should return false" {
        repeat(100) {
            list.clear()
            list.remove(models[it]) shouldBe false
        }
    }

    "add same instance should be ok" {
        repeat(100) { value ->
            repeat(100) {
                shouldNotThrowAny { list.add(models[value]) }
            }
        }
        list.size shouldBe 100*100
    }

    "not supported that not have unique hashcode implementation" {
        @Suppress("NAME_SHADOWING")
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
            repeat(100) { list.add(models[it]) }
            repeat(100) { list.remove(models[it]) }
        }
        list.size shouldBe 0
    }

    "removeIf" {
        repeat(100) { list.add(models[it]) }
        repeat(100) { i ->
            list.reversedRemoveIf { models[i].identityHashCode() == it.identityHashCode() }
            println(list.size)
            println(list.array.size)
            list.size shouldBe 99 - i
            list.contains(models[i]) shouldBe false
        }
    }

    "removeLast" {
        repeat(100) { list.add(models[it]) }
        repeat(100) {
            list.removeLast() shouldBe models[99 - it]
        }
        list.size shouldBe 0
    }

    "complex test" {
        val follower = ArrayList<String>()

        fun assert() {
            list.toArray().contentEquals(follower.toArray()) shouldBe true
            list.toTypedArray().contentEquals(follower.toArray()) shouldBe true
        }

        (0 until 100).map { models[it] }.forEach {
            list.add(it)
            follower.add(it)
        }

        assert()

        (0..100).shuffled().subList(0, 50).map { models[it] }.forEach {
            list.remove(it)
            follower.remove(it)
        }

        assert()

        (0 until 10).map { models[it] }.forEach {
            list.add(it)
            follower.add(it)
        }

        assert()

        (0..100).shuffled().subList(0, 10).map { models[it] }.forEach {
            list.remove(it)
            follower.remove(it)
        }

        assert()
    }

    "replaceLast" {
        repeat(10) { list.add(models[it]) }
        val value = "toValue"
        list.replaceLast(value)
        list.last() shouldBe value
        val value2 = "toValue2"
        list.replaceLast { value2 }
        list.last() shouldBe value2
        list.clear()
        shouldThrowAny { list.replaceLast("trash") }
    }

    "index ordinal" {
        repeat(10) { list.add(models[it]) }
        list.remove(models[2])
    }
})