package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.ImageLinks
import lk.prathieshna.bookfinder.domain.local.ReadingMode
import lk.prathieshna.bookfinder.domain.local.VolumeInfo

class ApiVolumeInfo {
    @SerializedName("title")
    var title: String? = ""

    @SerializedName("subtitle")
    var subtitle: String? = ""

    @SerializedName("authors")
    var authors: List<String?>? = listOf()

    @SerializedName("publisher")
    var publisher: String? = ""

    @SerializedName("publishedDate")
    var publishedDate: String? = ""

    @SerializedName("description")
    var description: String? = ""

    @SerializedName("readingModes")
    var readingModes: ApiReadingMode? = ApiReadingMode()

    @SerializedName("pageCount")
    var pageCount: Int? = 0

    @SerializedName("printedPageCount")
    var printedPageCount: Int? = 0

    @SerializedName("averageRating")
    var averageRating: Double? = 0.0

    @SerializedName("ratingsCount")
    var ratingsCount: Int? = 0

    @SerializedName("printType")
    var printType: String? = ""

    @SerializedName("categories")
    var categories: List<String?>? = listOf()

    @SerializedName("maturityRating")
    var maturityRating: String? = ""

    @SerializedName("allowAnonLogging")
    var allowAnonLogging: Boolean? = false

    @SerializedName("contentVersion")
    var contentVersion: String? = ""

    @SerializedName("imageLinks")
    var imageLinks: ApiImageLinks? = ApiImageLinks()

    @SerializedName("language")
    var language: String? = ""

    @SerializedName("previewLink")
    var previewLink: String? = ""

    @SerializedName("infoLink")
    var infoLink: String? = ""

    @SerializedName("canonicalVolumeLink")
    var canonicalVolumeLink: String? = ""

    fun mapToLocal(): VolumeInfo {
        return VolumeInfo(
            title = title,
            subtitle = subtitle,
            authors = authors,
            publisher = publisher,
            publishedDate = publishedDate,
            description = description,
            readingModes = readingModes?.mapToLocal() ?: ReadingMode(),
            pageCount = pageCount,
            printedPageCount = printedPageCount,
            averageRating = averageRating,
            ratingsCount = ratingsCount,
            printType = printType,
            categories = categories,
            maturityRating = maturityRating,
            allowAnonLogging = allowAnonLogging,
            contentVersion = contentVersion,
            imageLinks = imageLinks?.mapToLocal() ?: ImageLinks(),
            language = language,
            previewLink = previewLink,
            infoLink = infoLink,
            canonicalVolumeLink = canonicalVolumeLink
        )
    }
}
