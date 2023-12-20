//package benches
//
//import mintlin.EventBus
//import mintlin.EventBusImp
//import mintlin.listeners
//import org.koin.core.Koin
//import org.koin.dsl.koinApplication
//import org.openjdk.jmh.annotations.*
//import kotlin.math.max
//
//@BenchmarkMode(Mode.AverageTime)
//@State(Scope.Thread)
//class NumOpBench {
//    lateinit var koin: Koin
//    lateinit var scope: org.koin.core.scope.Scope
//    lateinit var arr: Array<Any?>
//    var size: Int = 33
//
//    @Setup
//    fun setup() {
//        koin = koinApplication {  }.koin
//        scope = koin.createScope<Any>()
//        arr = Array(33) { "fasd" }
//    }
//
//    @Benchmark
//    fun arrayCopy() {
////        var i = 0
////        val newArr = Array(++size) { arr[++i % size] }
////        newArr[arr.size] = arr[0]
//        arrayOfNulls<Any>(10)
//        for (i in 0 until arr.size) {
//            arr[i]
//        }
//    }
//
//
//
//
//}