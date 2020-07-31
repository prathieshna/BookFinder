package lk.prathieshna.bookfinder.services

import lk.prathieshna.bookfinder.constant.Constants.Companion.GOOGLE_BOOKS_API_VERSION
import lk.prathieshna.bookfinder.constant.Constants.Companion.GOOGLE_BOOKS_PAGE_SIZE
import lk.prathieshna.bookfinder.domain.api.ApiSearchResult
import lk.prathieshna.bookfinder.middleware.retrofit.RetrofitClient
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET(GOOGLE_BOOKS_API_VERSION + "volumes")
    fun getVolumesBySearch(
        @Query("q") q: String,
        @Query("startIndex") startIndex: Int,
        @Query("maxResults") maxResults: Int? = GOOGLE_BOOKS_PAGE_SIZE,
        @Query("orderBy") orderBy: String? = null,
        @Query("projection") projection: String? = null,
        @Query("printType ") printType: String? = null,
        @Query("filter") filter: String? = null
    ): Call<ApiSearchResult>
}

fun apiService(): ApiService {
    val retrofit = RetrofitClient.instance
    return retrofit.create(ApiService::class.java)
}