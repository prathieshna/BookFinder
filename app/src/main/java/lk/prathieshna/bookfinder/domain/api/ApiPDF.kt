package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.PDF

class ApiPDF {
    @SerializedName("isAvailable")
    var isAvailable: Boolean? = false

    fun mapToLocal(): PDF {
        return PDF(
            isAvailable = isAvailable
        )
    }
}
