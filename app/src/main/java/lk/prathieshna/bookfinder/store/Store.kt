package lk.prathieshna.bookfinder.store

import lk.prathieshna.bookfinder.middleware.getNetworkMiddleware
import lk.prathieshna.bookfinder.middleware.middlewareHandler
import lk.prathieshna.bookfinder.reducers.getAppReducer
import lk.prathieshna.bookfinder.reducers.reducerHandler
import lk.prathieshna.bookfinder.state.AppState
import org.rekotlin.Store


val bookFinderStore = Store(
    reducer = getAppReducer(AppState(), reducerHandler),
    state = null,
    middleware = listOf(getNetworkMiddleware(middlewareHandler))
)
