package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.ReadingMode

class ApiReadingMode {
    @SerializedName("text")
    var text: Boolean? = false

    @SerializedName("image")
    var image: Boolean? = false

    fun mapToLocal(): ReadingMode {
        return ReadingMode(
            text = text,
            image = image
        )
    }
}
