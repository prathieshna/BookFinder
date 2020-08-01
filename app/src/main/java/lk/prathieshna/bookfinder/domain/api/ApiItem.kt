package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.Item

class ApiItem {
    @SerializedName("kind")
    var kind: String? = ""

    @SerializedName("id")
    var id: String? = ""

    @SerializedName("etag")
    var eTag: String? = ""

    @SerializedName("selfLink")
    var selfLink: String? = ""

    @SerializedName("volumeInfo")
    var volumeInfo: ApiVolumeInfo? = ApiVolumeInfo()

    @SerializedName("saleInfo")
    var saleInfo: ApiSaleInfo? = ApiSaleInfo()

    @SerializedName("accessInfo")
    var accessInfo: ApiAccessInfo? = ApiAccessInfo()

    fun mapToLocal(): Item {
        return Item(
            kind = kind,
            id = id,
            eTag = eTag,
            selfLink = selfLink,
            volumeInfo = volumeInfo?.mapToLocal(),
            saleInfo = saleInfo?.mapToLocal(),
            accessInfo = accessInfo?.mapToLocal()
        )
    }
}
