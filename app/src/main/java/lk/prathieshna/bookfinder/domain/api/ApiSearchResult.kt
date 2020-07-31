package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.domain.local.SearchResult

class ApiSearchResult {
    @SerializedName("kind")
    var kind: String? = ""

    @SerializedName("totalItems")
    var totalItems: Int? = 0

    @SerializedName("items")
    var items: List<ApiItem?>? = listOf()

    fun mapToLocal(): SearchResult {
        return SearchResult(
            kind = kind,
            totalItems = totalItems,
            items = items?.map { it?.mapToLocal() ?: Item() } ?: listOf()
        )
    }
}
