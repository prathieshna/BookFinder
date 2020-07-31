package lk.prathieshna.bookfinder.domain.local

data class Item(
    var kind: String? = "",
    var id: String? = "",
    var eTag: String? = "",
    var selfLink: String? = "",
    var volumeInfo: VolumeInfo? = VolumeInfo(),
    var saleInfo: SaleInfo? = SaleInfo(),
    var accessInfo: AccessInfo? = AccessInfo()
)
