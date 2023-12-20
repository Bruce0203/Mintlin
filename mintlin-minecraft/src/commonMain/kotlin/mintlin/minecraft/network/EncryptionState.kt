package mintlin.minecraft.network

import mintlin.lang.MutableDelegate
import mintlin.lang.notNull
import kotlin.properties.Delegates

class EncryptionState : MutableDelegate<Boolean> by Delegates.notNull(false)