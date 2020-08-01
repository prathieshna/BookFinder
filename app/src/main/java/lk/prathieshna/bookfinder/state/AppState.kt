package lk.prathieshna.bookfinder.state

import android.content.Context
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.constant.Constants.Companion.DEFAULT_IMAGE_URL
import lk.prathieshna.bookfinder.domain.local.ImageLinks
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.SearchResult
import lk.prathieshna.bookfinder.domain.local.VolumeInfo
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
    val searchResult: SearchResult? = SearchResult()
)

val getTotalItems: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        if (getSearchResult(state).totalItems != null && getSearchResult(state).totalItems ?: 0 > 0) {
            context.getString(R.string.search_meta, getSearchResult(state).totalItems)
        } else {
            context.getString(R.string.search_meta_not_available)
        }
    }

val getSearchResult: (state: UdfBaseState<AppState>) -> SearchResult =
    { state -> state.state.searchResult ?: SearchResult() }

val getVolumes: (state: UdfBaseState<AppState>) -> List<Item> =
    { state -> getSearchResult(state).items?.map { it ?: Item() } ?: listOf() }

val getVolume: (state: UdfBaseState<AppState>, position: Int) -> Item =
    { state, position -> getVolumes(state)[position] }

val getVolumeInfo: (state: UdfBaseState<AppState>, position: Int) -> VolumeInfo =
    { state, position -> getVolume(state, position).volumeInfo ?: VolumeInfo() }

val getVolumeImageLinks: (state: UdfBaseState<AppState>, position: Int) -> ImageLinks =
    { state, position -> getVolumeInfo(state, position).imageLinks ?: ImageLinks() }

val getVolumeName: (state: UdfBaseState<AppState>, position: Int, context: Context) -> String =
    { state, position, context ->
        val title = getVolumeInfo(state, position).title
        if (title != null && title.isNotEmpty() && title.isNotBlank()) title
        else context.getString(R.string.not_available)
    }

val getVolumeDescription: (state: UdfBaseState<AppState>, position: Int, context: Context) -> String =
    { state, position, context ->
        val description = getVolumeInfo(state, position).description
        if (description != null && description.isNotEmpty() && description.isNotBlank()) description
        else context.getString(R.string.not_available)
    }

val getVolumeThumbnailImageURL: (state: UdfBaseState<AppState>, position: Int) -> String =
    { state, position ->
        val thumbnail = getVolumeImageLinks(state, position).thumbnail
        if (thumbnail != null && thumbnail.isNotEmpty() && thumbnail.isNotBlank()) thumbnail
        else DEFAULT_IMAGE_URL
    }

val getVolumeAuthors: (state: UdfBaseState<AppState>, position: Int, context: Context) -> String =
    { state, position, context ->
        val authors = getVolumeInfo(state, position).authors
        var string = ""
        if (authors != null && authors.isNotEmpty()) {
            authors.forEach { string = "$string $it" }
            if (string.isNotEmpty() && string.isNotBlank()) {
                string.trim()
            } else {
                context.getString(R.string.not_available)
            }
        } else context.getString(R.string.not_available)
    }