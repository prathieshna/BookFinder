package lk.prathieshna.bookfinder.middleware

import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.state.AppState
import org.rekotlin.Middleware

internal val NetworkMiddleware: Middleware<AppState> = { dispatch, _ ->
    { next ->
        { action ->
            when (action) {
                is GetVolumesBySearch.Request -> getVolumesBySearch(dispatch, action)
            }
            next(action)
        }
    }
}

