package lk.prathieshna.bookfinder.middleware

import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.state.UdfBaseState
import org.rekotlin.Action
import org.rekotlin.DispatchFunction
import org.rekotlin.Middleware

typealias MiddleWareHandler = (DispatchFunction, Action) -> Unit

fun <T> getNetworkMiddleware(handler: MiddleWareHandler): Middleware<UdfBaseState<T>> {
    return { dispatch: DispatchFunction, _ ->
        { next ->
            { action ->
                handler(dispatch, action)
                next(action)
            }
        }
    }
}

val middlewareHandler: MiddleWareHandler = { dispatch: DispatchFunction, action: Action ->
    when (action) {
        is GetVolumesBySearch.Request -> {
            getVolumesBySearch(dispatch, action)
        }
    }
}