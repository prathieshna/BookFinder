package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.AccessInfo
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.SaleInfo
import lk.prathieshna.bookfinder.domain.local.VolumeInfo

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
            volumeInfo = volumeInfo?.mapToLocal() ?: VolumeInfo(),
            saleInfo = saleInfo?.mapToLocal() ?: SaleInfo(),
            accessInfo = accessInfo?.mapToLocal() ?: AccessInfo()
        )
    }
}
