package lk.prathieshna.bookfinder.domain.api

import com.google.gson.annotations.SerializedName

data class OpenLibraryWork(
    @SerializedName("key")
    val key: String? = null,

    @SerializedName("title")
    val title: String? = null,

    @SerializedName("authors")
    val authors: List<AuthorReference>? = null,

    @SerializedName("description")
    val description: Any? = null, // Can be String or Object

    @SerializedName("subjects")
    val subjects: List<String>? = null,

    @SerializedName("ratings_average")
    val ratingsAverage: Float? = null,

    @SerializedName("ratings_count")
    val ratingsCount: Int? = null,

    @SerializedName("want_to_read_count")
    val wantToReadCount: Int? = null,

    @SerializedName("currently_reading_count")
    val currentlyReadingCount: Int? = null,

    @SerializedName("already_read_count")
    val alreadyReadCount: Int? = null
)

data class AuthorReference(
    @SerializedName("author")
    val author: AuthorKey? = null
)

data class AuthorKey(
    @SerializedName("key")
    val key: String? = null
)
