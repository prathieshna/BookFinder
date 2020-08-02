package lk.prathieshna.bookfinder.reducers

import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import org.rekotlin.Action

fun setSelectedReducer(
    action: Action,
    state: UdfBaseState<AppState>
): UdfBaseState<AppState> {
    when (action as GetVolumeByID) {
        is GetVolumeByID.Perform -> {
            val data = action as GetVolumeByID.Perform
            val newAppState = state.state.copy(selectedItem = data.selectedItem)

            return updateActionsStateStatus(
                state, action.getId(), GetVolumeByID.Success(action.getId()), newAppState
            )
        }
        is GetVolumeByID.Failure -> {
            return updateActionsStateStatus(
                state, action.getId(), GetVolumeByID.Failure(action.error, action.getId())
            )
        }
    }
    return state
}