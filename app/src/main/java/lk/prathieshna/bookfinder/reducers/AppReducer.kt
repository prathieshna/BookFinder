package lk.prathieshna.bookfinder.reducers

import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.state.AppState
import org.rekotlin.Action

fun updateActionsStateStatus(state: AppState, actionId: String?, action: BaseAction): AppState {
    if (actionId != null) {
        val statusMap = state.systemStateUpdateTracker
            .toMutableMap()
            .filterKeys { it != actionId }
            .toMutableMap()
        statusMap[actionId] = action
        return state.copy(systemStateUpdateTracker = statusMap.toMap())
    }
    return state
}

fun appReducers(action: Action, state: AppState?): AppState {
    val appState = state ?: AppState()

    when (action) {
        is GetVolumesBySearch -> {
            return getVolumesBySearchReducer(action, appState)
        }
    }
    return appState
}