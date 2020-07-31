package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.ImageLinks

class ApiImageLinks {
    @SerializedName("smallThumbnail")
    var smallThumbnail: String? = ""

    @SerializedName("thumbnail")
    var thumbnail: String? = ""

    @SerializedName("small")
    var small: String? = ""

    @SerializedName("medium")
    var medium: String? = ""

    @SerializedName("large")
    var large: String? = ""

    @SerializedName("extraLarge")
    var extraLarge: String? = ""

    fun mapToLocal(): ImageLinks {
        return ImageLinks(
            smallThumbnail = smallThumbnail,
            thumbnail = thumbnail,
            small = small,
            medium = medium,
            large = large,
            extraLarge = extraLarge
        )
    }
}
