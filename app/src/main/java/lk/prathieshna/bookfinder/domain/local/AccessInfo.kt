package lk.prathieshna.bookfinder.domain.local

data class AccessInfo(
    var country: String? = "",
    var viewability: Viewability = Viewability.UNKNOWN,
    var embeddable: Boolean? = false,
    var publicDomain: Boolean? = false,
    var textToSpeechPermission: String? = "",
    var ePub: EPub? = EPub(),
    var pdf: PDF? = PDF(),
    var webReaderLink: String? = "",
    var accessViewStatus: String? = "",
    var quoteSharingAllowed: Boolean? = false
)
