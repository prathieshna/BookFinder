package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.AccessInfo

class ApiAccessInfo {
    @SerializedName("country")
    var country: String? = ""

    @SerializedName("viewability")
    var viewability: String? = ""

    @SerializedName("embeddable")
    var embeddable: Boolean? = false

    @SerializedName("publicDomain")
    var publicDomain: Boolean? = false

    @SerializedName("textToSpeechPermission")
    var textToSpeechPermission: String? = ""

    @SerializedName("epub")
    var ePub: ApiEPub? = ApiEPub()

    @SerializedName("pdf")
    var pdf: ApiPDF? = ApiPDF()

    @SerializedName("webReaderLink")
    var webReaderLink: String? = ""

    @SerializedName("accessViewStatus")
    var accessViewStatus: String? = ""

    @SerializedName("quoteSharingAllowed")
    var quoteSharingAllowed: Boolean? = false

    fun mapToLocal(): AccessInfo {
        return AccessInfo(
            country = country,
            viewability = viewability,
            embeddable = embeddable,
            publicDomain = publicDomain,
            textToSpeechPermission = textToSpeechPermission,
            ePub = ePub?.mapToLocal(),
            pdf = pdf?.mapToLocal(),
            webReaderLink = webReaderLink,
            accessViewStatus = accessViewStatus,
            quoteSharingAllowed = quoteSharingAllowed
        )
    }
}
