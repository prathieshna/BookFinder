package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.SaleInfo

class ApiSaleInfo {
    @SerializedName("country")
    var country: String? = ""

    @SerializedName("saleability")
    var saleability: String? = ""

    @SerializedName("isEbook")
    var isEBook: Boolean? = false

    fun mapToLocal(): SaleInfo {
        return SaleInfo(
            country = country,
            saleability = saleability,
            isEBook = isEBook
        )
    }
}
