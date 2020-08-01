package lk.prathieshna.bookfinder.actions

import android.content.Context
import lk.prathieshna.bookfinder.constant.Constants.Companion.GOOGLE_BOOKS_PAGE_SIZE
import lk.prathieshna.bookfinder.domain.local.ApiError
import lk.prathieshna.bookfinder.domain.local.SearchResult
import lk.prathieshna.bookfinder.state.ActionStatus

sealed class GetVolumesBySearch(
    baseId: String? = "",
    actionStatus: ActionStatus? = ActionStatus.INIT,
    error: ApiError? = null
) : BaseAction(baseId, actionStatus, error) {
    class Request(
        val q: String,
        val startIndex: Int,
        val maxResults: Int? = GOOGLE_BOOKS_PAGE_SIZE,
        val orderBy: String? = null,
        val projection: String? = null,
        val printType: String? = null,
        val filter: String? = null,
        val context: Context,
        actionId: String?
    ) : GetVolumesBySearch(baseId = actionId)

    class Perform(
        val searchResult: SearchResult,
        val startIndex: Int,
        actionId: String?
    ) : GetVolumesBySearch(baseId = actionId)

    class Success(actionId: String?) :
        GetVolumesBySearch(baseId = actionId, actionStatus = ActionStatus.COMPLETED)

    class Failure(actionError: ApiError?, actionId: String?) : GetVolumesBySearch(
        baseId = actionId,
        actionStatus = ActionStatus.ERROR,
        error = actionError
    )
}