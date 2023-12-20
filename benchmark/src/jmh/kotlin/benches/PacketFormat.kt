//package benches
//
//import kotlinx.serialization.Serializable
//import kotlinx.serialization.serializer
//import mintlin.format.packet.PacketFormat
//import mintlin.serializer.VarInt
//import org.openjdk.jmh.annotations.*
//import java.nio.ByteBuffer
//
//@BenchmarkMode(Mode.AverageTime)
//@State(Scope.Thread)
//class SampleBench {
//
//    @Serializable
//    class TestPacket(
//        val name: VarInt,
//        val x: Double,
//        val y: Double,
//        val z: Double,
//    )
//
//
//    @Benchmark
//    fun test() {
//        PacketFormat.encodeToByteArray(serializer<TestPacket>(), TestPacket(393, 10.124, 1390.0, 394.24))
//    }
//}