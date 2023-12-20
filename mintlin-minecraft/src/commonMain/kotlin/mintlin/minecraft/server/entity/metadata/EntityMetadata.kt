package mintlin.minecraft.server.entity.metadata

import mintlin.minecraft.datastructure.*

open class EntityMetadata(override val metadata: Metadata) : MetadataHolder

private val entityByte0 = MetadataProperty<MetadataHolder, Byte>(id = 0, MetadataType.Byte) { 0 }
var EntityMetadata.isOnFire by FlagDelegate(0x01, entityByte0)
var EntityMetadata.isCrouching by FlagDelegate(0x02, entityByte0)
var EntityMetadata.isSprinting by FlagDelegate(0x08, entityByte0)
var EntityMetadata.isSwimming by FlagDelegate(0x10, entityByte0)
var EntityMetadata.isInvisible by FlagDelegate(0x20, entityByte0)
var EntityMetadata.hasGlowingEffect by FlagDelegate(0x40, entityByte0)
var EntityMetadata.isFlyingWithElytra by FlagDelegate(0x80.toByte(), entityByte0)
var EntityMetadata.airTicks by MetadataProperty(id = 0, MetadataType.VarInt) { 300 }
var EntityMetadata.customName by NullableMetadataProperty<EntityMetadata, Chat>(id = 2, MetadataType.OptChat)
var EntityMetadata.isCustomNameVisible by MetadataProperty(id = 3, MetadataType.Boolean) { false }
var EntityMetadata.isSilent by MetadataProperty(id = 4, MetadataType.Boolean) { false }
var EntityMetadata.hasNoGravity by MetadataProperty(id = 5, MetadataType.Boolean) { false }
var EntityMetadata.pose by MetadataProperty(id = 6, MetadataType.Pose) { Pose.Standing }
var EntityMetadata.ticksFrozenInPowderedSnow by MetadataProperty(id = 7, MetadataType.VarInt) { 0 }
