package lk.prathieshna.bookfinder.middleware

import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import org.rekotlin.Action
import org.rekotlin.DispatchFunction

val middlewareHandler: MiddleWareHandler = { dispatch: DispatchFunction, action: Action ->
    when (action) {
        is GetVolumesBySearch.Request -> {
            getVolumesBySearch(dispatch, action)
        }
        is GetVolumeByID.Request -> {
            getVolumeByID(dispatch, action)
        }
    }
}