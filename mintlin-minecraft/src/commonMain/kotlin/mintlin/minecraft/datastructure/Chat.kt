@file:OptIn(ExperimentalSerializationApi::class)
@file:Suppress("ArrayInDataClass")

package mintlin.minecraft.datastructure

import kotlinx.serialization.*
import kotlinx.serialization.builtins.ArraySerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encoding.decodeStructure
import kotlinx.serialization.encoding.encodeStructure
import mintlin.format.json.jsonSerializer
import mintlin.lang.classNameOf
import mintlin.serializer.*

data object JsonChatSerializer : KSerializer<Chat> by jsonSerializer(VarString262144Serializer, Chat)

@Serializable(JsonChatSerializer::class)
@SerialName("")
sealed interface Chat {
    val style: Style?

    companion object Serializer : KSerializer<Chat> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<Chat>()) {
            element<Boolean?>("bold")
            element<Boolean?>("italic")
            element<Boolean?>("underlined")
            element<Boolean?>("strikethrough")
            element<Boolean?>("obfuscated")
            element<String?>("font")
            element<String?>("color")
            element<String?>("insertion")
            element<ClickEvent?>("clickEvent")
            element<HoverEvent?>("hoverEvent")
            element<String?>("extra")

            element<String>("text")
            element<String>("translate")
            element<Array<String>>("with")
            element<String>("keybind")
            element<String>("selector")
            element<Score>("score")
        }

        override fun deserialize(decoder: Decoder): Chat = decoder.decodeStructure(descriptor) {
            var index = decodeElementIndex(descriptor)
            fun isIndexEquals(i: Int) = index == i
            fun nextIndex() = decodeElementIndex(descriptor).also { index = it }
            val style = if (index >= 11) null else Style(
                bold = if (isIndexEquals(0)) decodeSerializableElement(
                    descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                italic = if (isIndexEquals(1)) decodeSerializableElement(
                    descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                underlined = if (isIndexEquals(2)) decodeSerializableElement(
                    descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                strikethrough = if (isIndexEquals(3)) decodeSerializableElement(
                    descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                obfuscated = if (isIndexEquals(4)) decodeSerializableElement(
                    descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                font = if (isIndexEquals(5)) decodeSerializableElement(
                    descriptor,
                    index,
                    String.serializer()
                ).also { nextIndex() } else null,
                color = if (isIndexEquals(6)) decodeSerializableElement(
                    descriptor,
                    index,
                    String.serializer()
                ).also { nextIndex() } else null,
                insertion = if (isIndexEquals(7)) decodeSerializableElement(
                    descriptor,
                    index,
                    String.serializer()
                ).also { nextIndex() } else null,
                clickEvent = if (isIndexEquals(8)) decodeSerializableElement(
                    descriptor,
                    index,
                    ClickEvent
                ).also { nextIndex() } else null,
                hoverEvent = if (isIndexEquals(9)) decodeSerializableElement(
                    descriptor,
                    index,
                    HoverEvent
                ).also { nextIndex() } else null,
                extra = if (isIndexEquals(10)) decodeSerializableElement(
                    descriptor,
                    index,
                    Chat
                ).also { nextIndex() } else null
            )
            when (index) {
                11 -> StringComponent(
                    decodeSerializableElement(descriptor, index, UnescapedUnicodeStringSerializer),
                    style
                )

                12 -> TranslationComponent(
                    decodeSerializableElement(descriptor, index, String.serializer()),
                    if (decodeElementIndex(descriptor) != -1) decodeNullableSerializableElement(
                        descriptor,
                        13,
                        ArraySerializer(Chat)
                    ) else null, style
                )

                14 -> KeyBindComponent(decodeSerializableElement(descriptor, index, String.serializer()), style)
                15 -> SelectorComponent(decodeSerializableElement(descriptor, index, String.serializer()), style)
                16 -> ScoreComponent(decodeSerializableElement(descriptor, index, Score.serializer()), style)
                else -> throw RuntimeException("invalid chat component")
            }
        }

        override fun serialize(encoder: Encoder, value: Chat) = encoder.encodeStructure(descriptor) {
            val style = value.style
            fun Boolean?.nullIfFalse() = if (this == false) null else this
            encodeNullableSerializableElement(descriptor, 0, Boolean.serializer(), style?.bold.nullIfFalse())
            encodeNullableSerializableElement(descriptor, 1, Boolean.serializer(), style?.italic.nullIfFalse())
            encodeNullableSerializableElement(descriptor, 2, Boolean.serializer(), style?.underlined.nullIfFalse())
            encodeNullableSerializableElement(descriptor, 3, Boolean.serializer(), style?.strikethrough.nullIfFalse())
            encodeNullableSerializableElement(descriptor, 4, Boolean.serializer(), style?.obfuscated.nullIfFalse())
            encodeNullableSerializableElement(descriptor, 5, String.serializer(), style?.font)
            encodeNullableSerializableElement(descriptor, 6, String.serializer(), style?.color)
            encodeNullableSerializableElement(descriptor, 7, String.serializer(), style?.insertion)
            encodeNullableSerializableElement(descriptor, 8, ClickEvent, style?.clickEvent)
            encodeNullableSerializableElement(descriptor, 9, HoverEvent, style?.hoverEvent)
            encodeNullableSerializableElement(descriptor, 10, Chat, style?.extra)
            when (value) {
                is StringComponent -> encodeSerializableElement(
                    descriptor,
                    11,
                    UnescapedUnicodeStringSerializer,
                    value.text
                )

                is TranslationComponent -> {
                    encodeSerializableElement(descriptor, 12, String.serializer(), value.translate)
                    encodeNullableSerializableElement(descriptor, 13, ArraySerializer(Chat), value.with)
                }

                is KeyBindComponent -> encodeSerializableElement(descriptor, 14, String.serializer(), value.keyBind)
                is SelectorComponent -> encodeSerializableElement(descriptor, 15, String.serializer(), value.selector)
                is ScoreComponent -> encodeSerializableElement(descriptor, 16, Score.serializer(), value.score)
                is NbtComponent -> TODO()
            }
        }
    }
}

@Serializable
class StringComponent private constructor(val text: String, override val style: Style? = null) : Chat {
    companion object {
        operator fun invoke(text: String, style: Style? = null) = StringComponent(text.toUnicodeEscape(), style)
    }

    override fun toString() = "${classNameOf<StringComponent>()}(text = $text, $style)"
}

class TranslationComponent private constructor(
    val translate: String,
    val with: Array<Chat>? = null,
    override val style: Style? = null
) : Chat {
    companion object {
        operator fun invoke(translate: String, with: Array<Chat>? = null, style: Style? = null) =
            TranslationComponent(translate, with, style)
    }

    override fun toString() = "${classNameOf<TranslationComponent>()}(translate = $translate, with = $with, $style)"
}

class KeyBindComponent private constructor(
    @SerialName("keybind")
    val keyBind: String,
    override val style: Style? = null
) : Chat {
    companion object {
        operator fun invoke(keyBind: String, style: Style? = null) =
            KeyBindComponent(keyBind.toUnicodeEscape(), style)
    }

    override fun toString() = "${classNameOf<KeyBindComponent>()}(keyBind = $keyBind, $style)"
}

data class ScoreComponent(val score: Score, override val style: Style? = null) : Chat

@Serializable
data class Score(
    val name: String,
    val objective: String,
    val value: String
)

@Serializable
class SelectorComponent private constructor(val selector: String, override val style: Style? = null) : Chat {
    companion object {
        operator fun invoke(selector: String, style: Style? = null) =
            SelectorComponent(selector.toUnicodeEscape(), style)
    }

    override fun toString() = "${classNameOf<SelectorComponent>()}(selector = $selector, $style)"
}

@Serializable
data object NbtComponent : Chat {
    //Todo
    @Transient
    override val style: Style get() = TODO("Not yet implemented")
}

@Serializable
data class Style(
    val bold: Boolean = false,
    val italic: Boolean = false,
    val underlined: Boolean = false,
    val strikethrough: Boolean = false,
    val obfuscated: Boolean = false,
    val font: String? = null,
    val color: String? = null,
    val insertion: String? = null,
    val clickEvent: ClickEvent? = null,
    val hoverEvent: HoverEvent? = null,
    val extra: Chat? = null
) {
    @kotlinx.serialization.Serializer(Style::class)
    object Serializer : KSerializer<Style> {
        override fun deserialize(decoder: Decoder): Style = decoder.decodeStructure(descriptor) {
            var index = decodeElementIndex(descriptor)
            fun isIndexEquals(i: Int) = index == i
            fun nextIndex() = decodeElementIndex(descriptor).also { index = it }
            Style(
                bold = if (isIndexEquals(0)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                italic = if (isIndexEquals(1)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                underlined = if (isIndexEquals(2)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                strikethrough = if (isIndexEquals(3)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                obfuscated = if (isIndexEquals(4)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    Boolean.serializer()
                ).also { nextIndex() } else false,
                font = if (isIndexEquals(5)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    String.serializer()
                ).also { nextIndex() } else null,
                color = if (isIndexEquals(6)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    String.serializer()
                ).also { nextIndex() } else null,
                insertion = if (isIndexEquals(7)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    String.serializer()
                ).also { nextIndex() } else null,
                clickEvent = if (isIndexEquals(8)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    ClickEvent
                ).also { nextIndex() } else null,
                hoverEvent = if (isIndexEquals(9)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    HoverEvent
                ).also { nextIndex() } else null,
                extra = if (isIndexEquals(10)) decodeSerializableElement(
                    Chat.descriptor,
                    index,
                    Chat
                ).also { nextIndex() } else null
            )
        }


        override fun serialize(encoder: Encoder, value: Style) = encoder.encodeStructure(Chat.descriptor) {
            fun Boolean?.nullIfFalse() = if (this == false) null else this
            encodeNullableSerializableElement(Chat.descriptor, 0, Boolean.serializer(), value.bold.nullIfFalse())
            encodeNullableSerializableElement(Chat.descriptor, 1, Boolean.serializer(), value.italic.nullIfFalse())
            encodeNullableSerializableElement(Chat.descriptor, 2, Boolean.serializer(), value.underlined.nullIfFalse())
            encodeNullableSerializableElement(
                Chat.descriptor,
                3,
                Boolean.serializer(),
                value.strikethrough.nullIfFalse()
            )
            encodeNullableSerializableElement(Chat.descriptor, 4, Boolean.serializer(), value.obfuscated.nullIfFalse())
            encodeNullableSerializableElement(Chat.descriptor, 5, String.serializer(), value.font)
            encodeNullableSerializableElement(Chat.descriptor, 6, String.serializer(), value.color)
            encodeNullableSerializableElement(Chat.descriptor, 7, String.serializer(), value.insertion)
            encodeNullableSerializableElement(Chat.descriptor, 8, ClickEvent, value.clickEvent)
            encodeNullableSerializableElement(Chat.descriptor, 9, HoverEvent, value.hoverEvent)
            encodeNullableSerializableElement(Chat.descriptor, 10, Chat, value.extra)
        }
    }
}

@Serializable
sealed interface ClickEvent {
    @Serializable
    @SerialName("open_url")
    data class OpenURL(val url: String) : ClickEvent

    @Serializable
    @SerialName("run_command")
    data class RunCommand(val command: String) : ClickEvent

    @Serializable
    @SerialName("suggest_command")
    data class SuggestCommand(val command: String) : ClickEvent

    @Serializable
    @SerialName("change_page")
    data class ChangePage(val page: Int) : ClickEvent

    @Serializable
    @SerialName("copy_to_clipboard")
    data class CopyToClipboard(val text: String) : ClickEvent

    @OptIn(ExperimentalSerializationApi::class)
    companion object Serializer : KSerializer<ClickEvent> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<ClickEvent>()) {
            element<String>("action")
            element<String>("value")
        }

        override fun deserialize(decoder: Decoder): ClickEvent = decoder.decodeStructure(descriptor) {
            when (decodeStringElement(descriptor, 0)) {
                OpenURL.serializer().descriptor.serialName -> OpenURL(
                    decodeSerializableElement(
                        descriptor,
                        1,
                        String.serializer()
                    )
                )

                RunCommand.serializer().descriptor.serialName -> RunCommand(
                    decodeSerializableElement(
                        descriptor,
                        1,
                        String.serializer()
                    )
                )

                SuggestCommand.serializer().descriptor.serialName -> SuggestCommand(
                    decodeSerializableElement(
                        descriptor,
                        1,
                        String.serializer()
                    )
                )

                ChangePage.serializer().descriptor.serialName -> ChangePage(
                    decodeSerializableElement(
                        descriptor,
                        1,
                        Int.serializer()
                    )
                )

                CopyToClipboard.serializer().descriptor.serialName -> CopyToClipboard(
                    decodeSerializableElement(
                        descriptor,
                        1,
                        String.serializer()
                    )
                )

                else -> throw RuntimeException("unknown chat component")
            }
        }

        override fun serialize(encoder: Encoder, value: ClickEvent) {
            encoder.encodeStructure(descriptor) {
                when (value) {
                    is ChangePage -> {
                        encodeStringElement(descriptor, 0, ChangePage.serializer().descriptor.serialName)
                        encodeIntElement(descriptor, 1, value.page)
                    }

                    is CopyToClipboard -> {
                        encodeStringElement(descriptor, 0, CopyToClipboard.serializer().descriptor.serialName)
                        encodeStringElement(descriptor, 1, value.text)
                    }

                    is OpenURL -> {
                        encodeStringElement(descriptor, 0, OpenURL.serializer().descriptor.serialName)
                        encodeStringElement(descriptor, 1, value.url)
                    }

                    is RunCommand -> {
                        encodeStringElement(descriptor, 0, RunCommand.serializer().descriptor.serialName)
                        encodeStringElement(descriptor, 1, value.command)
                    }

                    is SuggestCommand -> {
                        encodeStringElement(descriptor, 0, SuggestCommand.serializer().descriptor.serialName)
                        encodeStringElement(descriptor, 1, value.toString())
                    }
                }
            }
        }
    }
}

@Serializable(HoverEvent.Serializer::class)
sealed interface HoverEvent {
    @Serializable
    @SerialName("show_text")
    data class ShowText(val contents: Contents) : HoverEvent {
        @Serializable(Contents.Serializer::class)
        sealed interface Contents {
            data class String(val value: kotlin.String) : Contents
            data class Component(val value: Chat) : Contents
            companion object Serializer : KSerializer<Contents> {
                override val descriptor = buildClassSerialDescriptor(classNameOf<Contents>()) {
                    element<Chat>("contents")
                }

                override fun deserialize(decoder: Decoder): Contents {
                    val polymorphicDecoder = decoder as mintlin.format.PolymorphicDecoder
                    return if (decoder.decodeValue() is kotlin.String)
                        String(polymorphicDecoder.decodeString())
                    else decoder.decodeStructure(descriptor) {
                        Component(Chat.deserialize(this as Decoder))
                    }
                }

                override fun serialize(encoder: Encoder, value: Contents) {
                    when (value) {
                        is Component -> encoder.encodeSerializableValue(Chat, value.value)
                        is String -> encoder.encodeSerializableValue(kotlin.String.serializer(), value.value)
                    }
                }
            }
        }
    }

    @Serializable
    @SerialName("show_item")
    data class ShowItem(
        val id: String,
        val count: Int,
        val tag: ItemNbt
    ) : HoverEvent

    @Serializable
    @SerialName("show_entity")
    data class ShowEntity(
        val id: UUID,
        val type: String? = null,
        @Serializable(Chat.Serializer::class)
        val name: Chat? = null
    ) : HoverEvent

    companion object Serializer : KSerializer<HoverEvent> {
        override val descriptor = buildClassSerialDescriptor(classNameOf<HoverEvent>()) {
            element<String>("action")
            element<String>("contents")
        }

        override fun deserialize(decoder: Decoder): HoverEvent = decoder.decodeStructure(descriptor) {
            when (decodeStringElement(
                descriptor,
                decodeElementIndex(descriptor)
            ).also { decodeElementIndex(descriptor) }) {
                ShowText.serializer().descriptor.serialName -> ShowText(
                    decodeSerializableElement(
                        descriptor,
                        1,
                        ShowText.Contents.serializer()
                    )
                )

                ShowEntity.serializer().descriptor.serialName -> decodeSerializableElement(
                    descriptor,
                    1,
                    ShowEntity.serializer()
                )

                ShowItem.serializer().descriptor.serialName -> decodeSerializableElement(
                    descriptor,
                    1,
                    ShowItem.serializer()
                )

                else -> throw RuntimeException("unknown hover event action")
            }
        }

        override fun serialize(encoder: Encoder, value: HoverEvent) = encoder.encodeStructure(descriptor) {
            when (value) {
                is ShowText -> {
                    encodeStringElement(descriptor, 0, ShowText.serializer().descriptor.serialName)
                    when (val contents = value.contents) {
                        is ShowText.Contents.Component ->
                            encodeSerializableElement(descriptor, 1, Chat, contents.value)

                        is ShowText.Contents.String -> encodeStringElement(descriptor, 1, contents.value)
                    }
                }

                is ShowEntity -> {
                    encodeStringElement(descriptor, 0, ShowEntity.serializer().descriptor.serialName)
                    encodeSerializableElement(descriptor, 1, ShowEntity.serializer(), value)
                }

                is ShowItem -> {
                    encodeStringElement(descriptor, 0, ShowItem.serializer().descriptor.serialName)
                    encodeSerializableElement(descriptor, 1, ShowItem.serializer(), value)
                }
            }
        }
    }
}


