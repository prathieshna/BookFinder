package lk.prathieshna.bookfinder.reducers

import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import org.rekotlin.Action

val reducerHandler: ReducerHandler<AppState> = { action: Action, appState: UdfBaseState<AppState> ->
    when (action) {
        is GetVolumesBySearch -> {
            getVolumesBySearchReducer(action, appState)
        }
        is GetVolumeByID -> {
            setSelectedReducer(action, appState)
        }
        else -> appState
    }
}
