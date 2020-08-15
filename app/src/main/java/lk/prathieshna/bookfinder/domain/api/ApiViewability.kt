package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName

enum class ApiViewability(val value: String) {
    @SerializedName("PARTIAL")
    PARTIAL("PARTIAL"),

    @SerializedName("ALL_PAGES")
    ALL_PAGES("ALL_PAGES"),

    @SerializedName("NO_PAGES")
    NO_PAGES("NO_PAGES"),

    @SerializedName("UNKNOWN")
    UNKNOWN("UNKNOWN")
}