package mintlin.minecraft.server.entity.metadata

import mintlin.minecraft.datastructure.*

class PlayerMetadata(metadata: Metadata) : LivingEntityMetadata(metadata)

var PlayerMetadata.additionalHearts by MetadataProperty(id = 15, MetadataType.Float) { 0F }
var PlayerMetadata.score by MetadataProperty(id = 16, MetadataType.VarInt) { 0 }
private val playerByte17 = MetadataProperty<PlayerMetadata, Byte>(id = 17, MetadataType.Byte) { 0 }
var PlayerMetadata.isCapeEnabled by FlagDelegate(0x01, playerByte17)
var PlayerMetadata.isJacketEnabled by FlagDelegate(0x02, playerByte17)
var PlayerMetadata.isLeftSleeveEnabled by FlagDelegate(0x04, playerByte17)
var PlayerMetadata.isRightSleeveEnabled by FlagDelegate(0x08, playerByte17)
var PlayerMetadata.isLeftPantsLegEnabled by FlagDelegate(0x10, playerByte17)
var PlayerMetadata.isRightPantsLegEnabled by FlagDelegate(0x20, playerByte17)
var PlayerMetadata.isHatEnabled by FlagDelegate(0x40, playerByte17)
private var PlayerMetadata.mainHandByte by MetadataProperty<PlayerMetadata, Byte>(id = 18, MetadataType.Byte) { 1 }
var PlayerMetadata.mainHand: MainHand
    get() = MainHand.entries[mainHandByte.toInt()]
    set(value) {
        mainHandByte = value.ordinal.toByte()
    }
var PlayerMetadata.leftShoulderEntityData by NullableMetadataProperty<PlayerMetadata, Unit>(id = 19, MetadataType.NBT)
var PlayerMetadata.rightShoulderEntityData by NullableMetadataProperty<PlayerMetadata, Unit>(id = 20, MetadataType.NBT)

