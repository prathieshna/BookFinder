package lk.prathieshna.bookfinder.reducers

import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.getVolumes
import org.rekotlin.Action

fun getVolumesBySearchReducer(action: Action, state: AppState): AppState {
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

            val localState = state.copy(searchResult = action.searchResult.copy(items = results))

            return updateActionsStateStatus(
                localState, action.getId(), GetVolumesBySearch.Success(action.getId())
            )
        }
    }
    return state
}