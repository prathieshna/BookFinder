package lk.prathieshna.bookfinder.domain.local

data class SearchResult(
    var kind: String? = "",
    var totalItems: Int? = 0,
    var items: List<Item?>? = listOf()
)
