package lk.prathieshna.bookfinder.reducers

import lk.prathieshna.bookfinder.actions.RemoveStateStatus
import lk.prathieshna.bookfinder.state.AppState

import org.rekotlin.Action

fun removeStateStatus(action: Action, state: AppState): AppState {
    when (action as RemoveStateStatus) {
        is RemoveStateStatus.Perform -> {
            val data = action as RemoveStateStatus.Perform
            val statusMap = state.systemStateUpdateTracker
                .toMutableMap()
                .filterKeys { it != data.uuid }
            return state.copy(systemStateUpdateTracker = statusMap) //updateActionsStateStatus(localState, action.getId(), RemoveStateStatus.Success(action.getId()!!))
        }
    }
    return state
}