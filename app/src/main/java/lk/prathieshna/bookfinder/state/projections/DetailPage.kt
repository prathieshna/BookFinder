package lk.prathieshna.bookfinder.state.projections

import android.content.Context
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.constant.Constants
import lk.prathieshna.bookfinder.domain.local.ImageLinks
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.VolumeInfo
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState

val getSelectedItem: (state: UdfBaseState<AppState>) -> Item =
    { state -> state.state.selectedItem ?: Item() }

val getSelectedItemVolumeInfo: (state: UdfBaseState<AppState>) -> VolumeInfo =
    { state -> getSelectedItem(state).volumeInfo ?: VolumeInfo() }

val getSelectedItemVolumeName: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        val title = getSelectedItemVolumeInfo(state).title
        if (title != null && title.isNotEmpty() && title.isNotBlank()) title
        else context.getString(R.string.not_available)
    }

val getSelectedItemId: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        val id = getSelectedItem(state).id
        if (id != null && id.isNotEmpty() && id.isNotBlank()) id
        else context.getString(R.string.not_available)
    }

val getSelectedItemVolumeDescription: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        val description = getSelectedItemVolumeInfo(state).description
        if (description != null && description.isNotEmpty() && description.isNotBlank()) description
        else context.getString(R.string.not_available)
    }

val getSelectedItemVolumeSubtitle: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        val subtitle = getSelectedItemVolumeInfo(state).subtitle
        if (subtitle != null && subtitle.isNotEmpty() && subtitle.isNotBlank()) subtitle
        else context.getString(R.string.not_available)
    }

val getSelectedItemVolumeVolumeImageLinks: (state: UdfBaseState<AppState>) -> ImageLinks =
    { state -> getSelectedItemVolumeInfo(state).imageLinks ?: ImageLinks() }


val getSelectedItemVolumeThumbnailImageURL: (state: UdfBaseState<AppState>) -> String =
    { state ->
        val thumbnail = getSelectedItemVolumeVolumeImageLinks(state).thumbnail
        if (thumbnail != null && thumbnail.isNotEmpty() && thumbnail.isNotBlank()) thumbnail
        else Constants.DEFAULT_IMAGE_URL
    }

val getSelectedItemVolumeAuthors: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        val authors = getSelectedItemVolumeInfo(state).authors
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