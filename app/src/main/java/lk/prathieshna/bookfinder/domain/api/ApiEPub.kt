package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.EPub

class ApiEPub {
    @SerializedName("isAvailable")
    var isAvailable: Boolean? = false

    fun mapToLocal(): EPub {
        return EPub(
            isAvailable = isAvailable
        )
    }
}
