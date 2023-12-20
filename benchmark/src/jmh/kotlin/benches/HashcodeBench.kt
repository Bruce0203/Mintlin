//package benches
//
//import korlibs.crypto.fillRandomBytes
//import korlibs.datastructure.identityHashCode
//import org.openjdk.jmh.annotations.*
//
//@JvmInline
//value class InlineKey(val value: Int)
//
//class WrappedKey(val value: Int) {
//    override fun hashCode() = value
//    override fun equals(other: Any?) = value == other.identityHashCode()
//}
//
//@BenchmarkMode(Mode.AverageTime)
//@State(Scope.Thread)
//class SampleBench {
//    val sample = ByteArray(1000).apply { fillRandomBytes(this) }
//    val intA: Any = Any().hashCode()
//    val intB: Any = Any().hashCode()
//    class ClassA
//    class ClassB
//    val classA: Any = ClassA()
//    val classB: Any = ClassB()
//    val inlineA: Any = InlineKey(Any().hashCode())
//    val inlineB: Any = InlineKey(Any().hashCode())
//    val wrappedA: Any = WrappedKey(Any().hashCode())
//    val wrappedB: Any = WrappedKey(Any().hashCode())
//
//    @Benchmark
//    fun anyCastedKClassEquals() {
//        classA.identityHashCode() == classB.identityHashCode()
//    }
//
//    @Benchmark
//    fun anyCastedIntegerEquals() {
//        intA == intB
//    }
//
//    @Benchmark
//    fun wrappedInlineClassIntegerValueEquals() {
//        inlineA == inlineB
//    }
//
//    @Benchmark
//    fun wrappedWrappingClassIntegerValueEquals() {
//        wrappedA == wrappedB
//    }
//}