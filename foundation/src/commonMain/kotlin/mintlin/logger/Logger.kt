package mintlin.logger

import kotlinx.atomicfu.atomic
import mintlin.datastructure.scope.*

//TODO coloring by level!
interface Logger : Scope {
    var level: Level

    fun log(message: () -> Any)

    fun isEnabled(level: Level): Boolean = this.level.index >= level.index

    fun error(message: () -> Any) {
        if (isEnabled(Level.ERROR)) log(message)
    }

    fun warn(message: () -> Any) {
        if (isEnabled(Level.WARN)) log(message)
    }

    fun info(message: () -> Any) {
        if (isEnabled(Level.INFO)) log(message)
    }

    fun debug(message: () -> Any) {
        if (isEnabled(Level.DEBUG)) log(message)
    }

    fun trace(message: () -> Any) {
        if (isEnabled(Level.TRACE)) log(message)
    }

    enum class Level {
        ERROR, WARN, INFO, DEBUG, TRACE;
        val index = ordinal
    }

    companion object : ScopeFactoryDSL<Logger, Level> by scopedDSL({
        singleOf(::LoggerImp) bind Logger::class
    })
}

class LoggerImp internal constructor(scope: Scope, level: Logger.Level) : Logger, Scope by scope {
    override var level: Logger.Level by atomic(level)

    override fun log(message: () -> Any) = println(message())
}
