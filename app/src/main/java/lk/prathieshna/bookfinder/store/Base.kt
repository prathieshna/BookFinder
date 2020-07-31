package lk.prathieshna.bookfinder.store

import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.RemoveStateStatus
import lk.prathieshna.bookfinder.state.ActionStatus
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.getStateFlowStatusBySession
import org.rekotlin.StoreSubscriber
import java.util.*

interface Base : StoreSubscriber<AppState> {
    val state: AppState
    var actionSessionIds: ArrayList<String>

    fun getActionId(): String {
        val actionId = UUID.randomUUID().toString()
        actionSessionIds.add(actionId)
        return actionId
    }

    fun dispatchAction(action: BaseAction, showLoader: Boolean = true) {
        if (showLoader)
            showLoader()
        appStore.dispatch(action)
    }

    fun showLoader()
    fun hideLoader()

    override fun newState(state: AppState) {
        val ids: List<String> = this.actionSessionIds.toList()
        ids.forEach { updateActivity(state, it) }
    }

    private fun updateActivity(state: AppState, actionId: String) {
        getStateFlowStatusBySession(state, actionId)?.let { action ->
            if (action.status == ActionStatus.COMPLETED) {
                if (onStateUpdate(state, action))
                    hideLoader()
                appStore.dispatch(RemoveStateStatus.Perform(actionId, getActionId()))
            } else if (action.status == ActionStatus.ERROR) {
                onError(action)
                hideLoader()
                appStore.dispatch(RemoveStateStatus.Perform(actionId, getActionId()))
            }
        }
    }

    fun onStateUpdate(state: AppState, action: BaseAction): Boolean
    fun onError(action: BaseAction)
}