package lk.prathieshna.bookfinder.middleware

import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.domain.api.ApiItem
import lk.prathieshna.bookfinder.domain.local.ApiError
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.middleware.utils.parseError
import lk.prathieshna.bookfinder.services.apiService
import org.rekotlin.DispatchFunction
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

fun getVolumeByID(dispatch: DispatchFunction, action: GetVolumeByID.Request) {
    val service = apiService()

    val call = service.getVolumesByID(
        id = action.id
    )

    call.enqueue(object : Callback<ApiItem> {
        override fun onResponse(
            call: Call<ApiItem>?,
            responseApi: Response<ApiItem>?
        ) {
            responseApi?.let {
                if (responseApi.code() == 200) {
                    dispatch(
                        GetVolumeByID.Perform(
                            selectedItem = responseApi.body()?.mapToLocal() ?: Item(),
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

        override fun onFailure(call: Call<ApiItem>?, t: Throwable?) {
            val apiError = ApiError(
                500,
                t?.message ?: action.context.getString(R.string.unexpected_error)
            )
            throwError(apiError)
        }

        fun throwError(error: ApiError) {
            dispatch(GetVolumeByID.Failure(error, action.getId()))
        }
    })
}