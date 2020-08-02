package lk.prathieshna.bookfinder.state

import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.SearchResult
import org.rekotlin.StateType

data class UdfBaseState<T>(
    val state: T,
    val systemStateUpdateTracker: Map<String, BaseAction> = hashMapOf()

) : StateType


enum class ActionStatus {
    INIT, COMPLETED, ERROR
}


fun <T> getStateFlowStatusBySession(state: UdfBaseState<T>, sessionId: String): BaseAction? {
    return state.systemStateUpdateTracker[sessionId]
}

data class AppState(
    val searchResult: SearchResult? = SearchResult(),
    val selectedItem: Item? = Item()
)