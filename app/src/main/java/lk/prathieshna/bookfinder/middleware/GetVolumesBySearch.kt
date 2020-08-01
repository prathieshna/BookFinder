package lk.prathieshna.bookfinder.middleware

import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.domain.api.ApiSearchResult
import lk.prathieshna.bookfinder.domain.local.ApiError
import lk.prathieshna.bookfinder.domain.local.SearchResult
import lk.prathieshna.bookfinder.middleware.utils.parseError
import lk.prathieshna.bookfinder.services.apiService
import org.rekotlin.DispatchFunction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getVolumesBySearch(dispatch: DispatchFunction, action: GetVolumesBySearch.Request) {
    val service = apiService()

    val call = service.getVolumesBySearch(
        q = action.q,
        startIndex = action.startIndex,
        maxResults = action.maxResults,
        orderBy = action.orderBy,
        projection = action.projection,
        printType = action.printType,
        filter = action.filter
    )

    call.enqueue(object : Callback<ApiSearchResult> {
        override fun onResponse(
            call: Call<ApiSearchResult>?,
            responseApi: Response<ApiSearchResult>?
        ) {
            responseApi?.let {
                if (responseApi.code() == 200) {
                    dispatch(
                        GetVolumesBySearch.Perform(
                            searchResult = responseApi.body()?.mapToLocal() ?: SearchResult(),
                            startIndex = action.startIndex,
                            actionId = action.getId()
                        )
                    )
                } else {
                    parseError(responseApi.errorBody(), responseApi.code(), action.context)?.let {
                        throwError(it)
                    }
                }
            }
        }

        override fun onFailure(call: Call<ApiSearchResult>?, t: Throwable?) {
            val apiError = ApiError(
                500,
                t?.message ?: action.context.getString(R.string.unexpected_error)
            )
            throwError(apiError)
        }

        fun throwError(error: ApiError) {
            dispatch(GetVolumesBySearch.Failure(error, action.getId()))
        }
    })
}