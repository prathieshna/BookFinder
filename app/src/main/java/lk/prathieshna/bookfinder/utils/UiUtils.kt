package lk.prathieshna.bookfinder.utils

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.palette.graphics.Palette
import com.squareup.picasso.Picasso
import lk.prathieshna.bookfinder.R
import kotlin.math.max

fun getDominantColorFromImageURL(context: Context, url: String, callback: (Int) -> Unit) {
    Thread(Runnable {
        try {
            val bitmap = Picasso.get().load(url).get()
            Palette.Builder(bitmap).generate {
                it?.let { palette ->
                    var dominantColor = palette.getDominantColor(
                        ContextCompat.getColor(
                            context,
                            R.color.colorPrimaryDark
                        )
                    )
                    dominantColor = darkenColour(dominantColor, 0.75f)
                    callback(dominantColor)
                }
            }
        } catch (e: Exception) {
            Log.e("getDominantColor", e.message ?: "Error Occurred")
        }
    }).start()
}

fun darkenColour(dominantColor: Int, float: Float): Int {
    val a: Int = Color.alpha(dominantColor)
    val r: Int = Color.red(dominantColor)
    val g: Int = Color.green(dominantColor)
    val b: Int = Color.blue(dominantColor)

    return Color.argb(
        a,
        max((r * float), 0f).toInt(),
        max((g * float), 0f).toInt(),
        max((b * float), 0f).toInt()
    )
}