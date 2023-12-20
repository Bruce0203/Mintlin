package mintlin.io.compression

import io.kotest.core.spec.style.StringSpec
import mintlin.serializer.toUnicodeEscape

class UnicodeEscapeTest : StringSpec({
    "unicode escape" {
        println("안녕하세요".toUnicodeEscape())
    }
})