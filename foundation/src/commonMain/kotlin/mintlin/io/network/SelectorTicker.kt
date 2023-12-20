package mintlin.io.network

import kotlinx.datetime.Clock
import mintlin.datastructure.scope.*
import mintlin.lang.MutableDelegate
import mintlin.lang.notNull
import kotlin.properties.Delegates
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data object Tick

class ServerTickRateDelegate : MutableDelegate<Duration> by Delegates.notNull(initialValue = (1/20.0).seconds)

interface ServerPresenterFacade {
    fun run(onTick: () -> Unit)
}

interface SelectorTicker : ServerPresenterFacade, Scope {
    companion object : ScopeFactoryDSL<SelectorTicker, Selector> by scopedDSL({
        singleOf(::SelectorTickerImp) bind SelectorTicker::class
        singleOf(::ServerTickRateDelegate)
    })
}

class SelectorTickerImp(
    private val selector: Selector,
    private val scope: Scope
) : SelectorTicker, Scope by scope {
    private val tick by scope.get<ServerTickRateDelegate>()

    override fun run(onTick: () -> Unit) {
        val start = Clock.System.now().toEpochMilliseconds()
        var lastTick = 0L
        while (true) {
            val tickMillis = tick.inWholeMilliseconds
            selector.select()
            val now = Clock.System.now().toEpochMilliseconds()
            val uptime = now - start
            if (uptime - lastTick >= tickMillis) {
                onTick()
                lastTick += tickMillis
            }
        }
    }
}