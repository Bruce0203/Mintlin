package mintlin.minecraft.datastructure

import mintlin.datastructure.FastIdentityMap


interface Inventory {
    operator fun get(slot: Int): SlotItem?

    operator fun set(slot: Int, item: SlotItem?)
}

class InventoryImp : Inventory {
    private val size: Int = 36//todo change by entity type

    private val contents = FastIdentityMap<Int, SlotItem?>()

    override fun get(slot: Int): SlotItem? = contents[slot]

    override fun set(slot: Int, item: SlotItem?) {
        contents[slot] = item
    }
}