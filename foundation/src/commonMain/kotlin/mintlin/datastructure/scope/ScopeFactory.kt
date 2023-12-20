package mintlin.datastructure.scope

import mintlin.datastructure.FastArrayList

interface ScopeFactory<T> {
    fun createScope(): Scope

    fun createScopeAndLinkTo(vararg links: ScopeFactory<*>): Scope
    fun createScopeAndLinkTo(vararg links: Scope): Scope
}

class ScopeFactoryImp<T>(
    private val definition: ScopeDSL.() -> Unit
) : ScopeFactory<T>, ScopeFactoryDSL<T, Any> {
    override fun createScope(): Scope {
        val scope = ScopeImp()
        definition(scope)
        return scope
    }

    override fun createScopeAndLinkTo(vararg links: ScopeFactory<*>): Scope {
        val scope = ScopeImp(FastArrayList(links.size) {
            (links[it]).createScope() as ScopeImp
        })
        definition(scope)
        return scope
    }

    override fun createScopeAndLinkTo(vararg links: Scope): Scope {
        val scope: ScopeDSL = ScopeImp(FastArrayList(links))
        definition(scope)
        return scope
    }
}
