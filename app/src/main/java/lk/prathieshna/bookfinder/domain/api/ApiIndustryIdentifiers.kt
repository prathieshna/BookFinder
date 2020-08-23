package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.IndustryIdentifiers

class ApiIndustryIdentifiers {
    @SerializedName("type")
    var type: String? = ""

    @SerializedName("identifier")
    var identifier: String? = ""

    fun mapToLocal(): IndustryIdentifiers {
        return IndustryIdentifiers(
            type = type,
            identifier = identifier
        )
    }
}
