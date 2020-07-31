package lk.prathieshna.bookfinder.domain.local

data class VolumeInfo(
    var title: String? = "",
    var subtitle: String? = "",
    var authors: List<String?>? = listOf(),
    var publisher: String? = "",
    var publishedDate: String? = "",
    var description: String? = "",
    var readingModes: ReadingMode? = ReadingMode(),
    var pageCount: Int? = 0,
    var printedPageCount: Int? = 0,
    var averageRating: Double? = 0.0,
    var ratingsCount: Int? = 0,
    var printType: String? = "",
    var categories: List<String?>? = listOf(),
    var maturityRating: String? = "",
    var allowAnonLogging: Boolean? = false,
    var contentVersion: String? = "",
    var imageLinks: ImageLinks? = ImageLinks(),
    var language: String? = "",
    var previewLink: String? = "",
    var infoLink: String? = "",
    var canonicalVolumeLink: String? = ""
)
