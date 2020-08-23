package lk.prathieshna.bookfinder.state.projections

import android.content.Context
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.constant.Constants
import lk.prathieshna.bookfinder.domain.local.ImageLinks
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.SearchResult
import lk.prathieshna.bookfinder.domain.local.VolumeInfo
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState

val getSearchResult: (state: UdfBaseState<AppState>) -> SearchResult =
    { state -> state.state.searchResult ?: SearchResult() }

val getVolumes: (state: UdfBaseState<AppState>) -> List<Item> =
    { state -> getSearchResult(state).items?.map { it ?: Item() } ?: listOf() }

val getTotalItems: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        if (getSearchResult(state).totalItems != null && getSearchResult(state).totalItems ?: 0 > 0) {
            context.getString(R.string.search_meta, getSearchResult(state).totalItems)
        } else {
            context.getString(R.string.search_meta_not_available)
        }
    }

val getVolume: (state: UdfBaseState<AppState>, id: String) -> Item =
    { state, id -> getVolumes(state).first { it.id == id } }

val getVolumeInfo: (state: UdfBaseState<AppState>, id: String) -> VolumeInfo =
    { state, id -> getVolume(state, id).volumeInfo ?: VolumeInfo() }

val getVolumeImageLinks: (state: UdfBaseState<AppState>, id: String) -> ImageLinks =
    { state, id -> getVolumeInfo(state, id).imageLinks ?: ImageLinks() }

val getVolumeName: (state: UdfBaseState<AppState>, id: String, context: Context) -> String =
    { state, id, context ->
        val title = getVolumeInfo(state, id).title
        if (title != null && title.isNotEmpty() && title.isNotBlank()) title
        else context.getString(R.string.not_available)
    }

val getVolumeRatingCountString: (state: UdfBaseState<AppState>, id: String, context: Context) -> String =
    { state, id, context ->
        val ratingCount = getVolumeInfo(state, id).ratingsCount ?: 0
        when {
            ratingCount > 1 -> context.getString(R.string.rating_count_many, ratingCount)
            ratingCount == 1 -> context.getString(R.string.rating_count_1, ratingCount)
            else -> context.getString(R.string.rating_count_0)
        }
    }
val getVolumeRating: (state: UdfBaseState<AppState>, id: String) -> Float =
    { state, id ->
        getVolumeInfo(state, id).averageRating ?: 0F
    }

val getVolumeDescription: (state: UdfBaseState<AppState>, id: String, context: Context) -> String =
    { state, id, context ->
        val description = getVolumeInfo(state, id).description
        if (description != null && description.isNotEmpty() && description.isNotBlank()) description
        else context.getString(R.string.not_available)
    }

val getVolumeThumbnailImageURL: (state: UdfBaseState<AppState>, id: String) -> String =
    { state, id ->
        val thumbnail = getVolumeImageLinks(state, id).thumbnail
        if (thumbnail != null && thumbnail.isNotEmpty() && thumbnail.isNotBlank()) thumbnail
        else Constants.DEFAULT_IMAGE_URL
    }

val getVolumeAuthors: (state: UdfBaseState<AppState>, id: String, context: Context) -> String =
    { state, id, context ->
        val authors = getVolumeInfo(state, id).authors
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