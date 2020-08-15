package lk.prathieshna.bookfinder.state.projections

import android.content.Context
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState

val getSelectedItemEmbeddedURL: (state: UdfBaseState<AppState>, context: Context) -> String =
    { state, context ->
        context.getString(
            R.string.embedded_url,
            getSelectedItemId(state, context)
        )
    }