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


data class AppState(
    val searchResult: SearchResult = SearchResult(),

    val systemStateUpdateTracker: Map<String, BaseAction> = hashMapOf()
) : StateType

enum class ActionStatus {
    INIT, COMPLETED, ERROR
}

val getStateFlowStatusBySession: (state: AppState, sessionId: String) -> BaseAction? =
    { state, sessionId ->
        state.systemStateUpdateTracker[sessionId]
    }

val getVolumes: (state: AppState) -> List<Item> =
    { state -> state.searchResult.items?.map { it ?: Item() } ?: listOf() }

val getVolume: (state: AppState, position: Int) -> Item =
    { state, position -> state.searchResult.items?.get(position) ?: Item() }

val getVolumeInfo: (state: AppState, position: Int) -> VolumeInfo =
    { state, position -> getVolume(state, position).volumeInfo ?: VolumeInfo() }

val getVolumeImageLinks: (state: AppState, position: Int) -> ImageLinks =
    { state, position -> getVolumeInfo(state, position).imageLinks ?: ImageLinks() }

val getVolumeName: (state: AppState, position: Int, context: Context) -> String =
    { state, position, context ->
        val title = getVolumeInfo(state, position).title
        if (title != null && title.isNotEmpty() && title.isNotBlank()) title
        else context.getString(R.string.not_available)
    }

val getVolumeDescription: (state: AppState, position: Int, context: Context) -> String =
    { state, position, context ->
        val description = getVolumeInfo(state, position).description
        if (description != null && description.isNotEmpty() && description.isNotBlank()) description
        else context.getString(R.string.not_available)
    }

val getVolumeThumbnailImageURL: (state: AppState, position: Int) -> String =
    { state, position ->
        val thumbnail = getVolumeImageLinks(state, position).thumbnail
        if (thumbnail != null && thumbnail.isNotEmpty() && thumbnail.isNotBlank()) thumbnail
        else DEFAULT_IMAGE_URL
    }

val getVolumeAuthors: (state: AppState, position: Int, context: Context) -> String =
    { state, position, context ->
        val authors = getVolumeInfo(state, position).authors
        var string = ""
        if (authors != null && authors.isNotEmpty()) {
            authors.forEach { string = "$string $it" }
            if (string.isNotEmpty() && string.isNotBlank()) {
                string
            } else {
                context.getString(R.string.not_available)
            }
        } else context.getString(R.string.not_available)
    }