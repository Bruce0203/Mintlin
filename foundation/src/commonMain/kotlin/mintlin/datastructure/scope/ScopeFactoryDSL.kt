package mintlin.datastructure.scope

//todo: typesafe scope linkers like already implemented typesafe injection argument (maybe you should try...)
interface ScopeFactoryDSL<T, I> : ScopeFactory<T>

inline fun <reified T> scoped(noinline definition: ScopeDSL.() -> Unit): ScopeFactory<T> =
    ScopeFactoryImp(definition)

@Suppress("UNCHECKED_CAST")
inline fun <reified T, reified I> scopedDSL(noinline definition: ScopeDSL.() -> Unit): ScopeFactoryDSL<T, I> =
    ScopeFactoryImp<T>(definition) as ScopeFactoryDSL<T, I>

inline operator fun <reified T> ScopeFactoryDSL<T, T>.invoke(): T = createScope().get(T::class)

inline operator fun <T : ScopeFactoryDSL<T2, I>, reified T2, reified I> T.invoke(links: Array<ScopeFactory<*>>): T2 =
    createScopeAndLinkTo(*links).get(T2::class)

inline operator fun <T : ScopeFactory<T2>, reified T2> T.invoke(links: Array<Scope>): T2 =
    createScopeAndLinkTo(*links).get(T2::class)

inline operator fun <T : ScopeFactoryDSL<T2, Unit>, reified T2> T.invoke(vararg links: Scope): T2 =
    createScopeAndLinkTo(*links).get(T2::class)

inline operator fun <T : ScopeFactoryDSL<T2, I>, reified T2 : Any, reified I> T.invoke(injection: I, vararg links: Scope): T2 {
    val scope = createScopeAndLinkTo(*links) as ScopeImp
    scope.declare(I::class, injection)
    return scope.get<T2>()
}
