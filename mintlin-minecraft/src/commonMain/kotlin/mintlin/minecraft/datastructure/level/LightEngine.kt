package mintlin.minecraft.datastructure.level

import mintlin.minecraft.datastructure.Point3D
import java.util.*
import kotlin.system.exitProcess

class LightEngine {
    private val utils: SectionUtils = SectionUtils()
    private val fullbright: Byte = 7 // 14
    private val half: Byte = 3 // 10
    private val dark: Byte = 2 // 7
    lateinit var recalcArray: ByteArray
    var exposed = Array(16) { BooleanArray(16) }

    fun recalculateChunk(chunkData: ChunkData, light: Light) {
        exposed = Array(16) { BooleanArray(16) }
        for (e in exposed) {
            Arrays.fill(e, true)
        }
        val sections: List<ChunkSection> = chunkData.chunkSections.reversed()
        sections.forEachIndexed { i, section -> recalculateSection(section, i, light) }
    }

    private fun recalculateSection(section: ChunkSection, sectionIndex: Int, lights: Light) {
        recalcArray = ByteArray(ARRAY_SIZE)
        for (x in 0..15) {
            for (z in 0..15) {
                for (y in 15 downTo -1 + 1) {
                    if (!utils.lightCanPassThrough(section.blockStates.get(Point3D(x, y, z)))) {
                        exposed[x][z] = false
                    }
                    if (exposed[x][z]) {
                        set(utils.getCoordIndex(x, y, z), fullbright.toInt())
                    } else {
                        set(utils.getCoordIndex(x, y, z), dark.toInt())
                    }
                    set(utils.getCoordIndex(x, y, z), 15)
                }
            }
        }
        lights.skyLights[sectionIndex] = recalcArray
        lights.blockLights[sectionIndex] = recalcArray
    }

    // operation type: updating
    operator fun set(x: Int, y: Int, z: Int, value: Int) {
        this[x and 15 or (z and 15 shl 4) or (y and 15 shl 8)] = value
    }

    // https://github.com/PaperMC/Starlight/blob/6503621c6fe1b798328a69f1bca784c6f3ffcee3/src/main/java/ca/spottedleaf/starlight/common/light/SWMRNibbleArray.java#L410
    // operation type: updating
    operator fun set(index: Int, value: Int) {
        val shift = index and 1 shl 2
        val i = index ushr 1
        recalcArray[i] = (recalcArray[i].toInt() and (0xF0 ushr shift) or (value shl shift)).toByte()
    }

    companion object {
        //https://github.com/PaperMC/Starlight/blob/6503621c6fe1b798328a69f1bca784c6f3ffcee3/src/main/java/ca/spottedleaf/starlight/common/light/SWMRNibbleArray.java#L25
        const val ARRAY_SIZE = 16 * 16 * 16 / (8 / 4) // blocks / bytes per block
    }
}

class SectionUtils {
    private val dimension: Int

    constructor() {
        dimension = 16
    }

    constructor(dimension: Int) {
        this.dimension = dimension
    }

    /**
     * Util for light arrays
     * @param x only 0 - 16
     * @param y only 0 - 16
     * @param z only 0 - 16
     * @return index of block coordinate for light arrays
     */
    fun getCoordIndex(x: Int, y: Int, z: Int): Int {
        return y shl dimension / 2 or (z shl dimension / 4) or x
    }

    fun lightCanPassThrough(block: Int): Boolean {
        if (block == -1) exitProcess(0)
        return block == 0//TODO compare to isSolid property...
    }

    var printed = 0
    fun shrink(array: ByteArray): ByteArray {
        val shrunk = ByteArray(ARRAY_SIZE)
        var i = 0
        while (i < array.size) {
            val j = i + 1
            val iB = array[i]
            val jB = array[j]
            val merged = (array[i].toInt() shl 4 or array[j].toInt()).toByte()
            if (printed < 10) {
                println(Integer.toBinaryString(iB.toInt()))
                println(Integer.toBinaryString(jB.toInt()))
                println(Integer.toBinaryString(merged.toInt()))
                println(i)
                println(j)
                println("---------------")
                printed++
            }
            shrunk[i / 2] = (array[i].toInt() shl 4 or array[j].toInt()).toByte()
            i += 2
        }
        return shrunk
    }

    companion object {
        //https://github.com/PaperMC/Starlight/blob/6503621c6fe1b798328a69f1bca784c6f3ffcee3/src/main/java/ca/spottedleaf/starlight/common/light/SWMRNibbleArray.java#L25
        const val ARRAY_SIZE = 16 * 16 * 16 / (8 / 4) // blocks / bytes per block
    }
}
