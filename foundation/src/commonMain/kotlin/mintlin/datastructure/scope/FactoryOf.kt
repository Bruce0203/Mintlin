package mintlin.datastructure.scope

inline fun <reified R>
        ScopeDSL.factoryOf(crossinline block: () -> R) =
    factory(R::class) { block() }

inline fun <reified R, reified T1>
        ScopeDSL.factoryOf(crossinline block: (T1) -> R) =
    factory(R::class) { block(get()) }

inline fun <reified R, reified T1, reified T2>
        ScopeDSL.factoryOf(crossinline block: (T1, T2) -> R) =
    factory(R::class) { block(get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3) -> R) =
    factory(R::class) { block(get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4) -> R) =
    factory(R::class) { block(get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), ) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }


inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

inline fun <reified R, reified T1, reified T2, reified T3, reified T4, reified T5, reified T6, reified T7, reified T8, reified T9, reified T10, reified T11, reified T12, reified T13, reified T14, reified T15, reified T16, reified T17, reified T18, reified T19, reified T20, reified T21, reified T22>
        ScopeDSL.factoryOf(crossinline block: (T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, T17, T18, T19, T20, T21, T22) -> R) =
    factory(R::class) { block(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }

