package lk.prathieshna.bookfinder.store

import lk.prathieshna.bookfinder.middleware.NetworkMiddleware
import lk.prathieshna.bookfinder.reducers.appReducers
import org.rekotlin.Store

val appStore = Store(
    reducer = ::appReducers,
    state = null,
    middleware = listOf(NetworkMiddleware)
)