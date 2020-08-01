package lk.prathieshna.bookfinder.reducers

import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.getVolumes
import org.rekotlin.Action

fun getVolumesBySearchReducer(
    action: Action,
    state: UdfBaseState<AppState>
): UdfBaseState<AppState> {
    when (action as GetVolumesBySearch) {
        is GetVolumesBySearch.Perform -> {
            val data = action as GetVolumesBySearch.Perform
            var results: MutableList<Item> = getVolumes(state).toMutableList()

            if (data.startIndex != 0) {
                results.addAll(data.searchResult.items?.map { it ?: Item() } ?: listOf())
            } else {
                results = data.searchResult.items?.map { it ?: Item() }?.toMutableList()
                    ?: mutableListOf()
            }

            val newAppState =
                state.state.copy(searchResult = action.searchResult.copy(items = results))

            return updateActionsStateStatus(
                state, action.getId(), GetVolumesBySearch.Success(action.getId()), newAppState
            )
        }
        is GetVolumesBySearch.Failure -> {
            return updateActionsStateStatus(
                state, action.getId(), GetVolumesBySearch.Failure(action.error, action.getId())
            )
        }
    }
    return state
}