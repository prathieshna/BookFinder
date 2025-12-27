package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName

data class OpenLibraryRatings(
    @SerializedName("summary")
    val summary: RatingSummary? = null
)

data class RatingSummary(
    @SerializedName("average")
    val average: Float? = null,

    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("sortable")
    val sortable: Float? = null
)
