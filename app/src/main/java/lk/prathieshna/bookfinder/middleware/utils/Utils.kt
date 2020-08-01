package lk.prathieshna.bookfinder.middleware.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.api.ApiErrorMessage
import lk.prathieshna.bookfinder.domain.local.ApiError
import okhttp3.ResponseBody

fun parseError(errorResponse: ResponseBody?, code: Int, context: Context): ApiError? {
    if (code == 401) return ApiError(401, context.getString(R.string.unauthorized))
    val gSon = Gson()
    val type = object : TypeToken<ApiErrorMessage>() {}.type

    val apiErrorMessage: ApiErrorMessage = gSon.fromJson(errorResponse?.charStream(), type)
    return apiErrorMessage.error
}

fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
    var maxSize = 0
    for (i in lastVisibleItemPositions.indices) {
        if (i == 0) {
            maxSize = lastVisibleItemPositions[i]
        } else if (lastVisibleItemPositions[i] > maxSize) {
            maxSize = lastVisibleItemPositions[i]
        }
    }
    return maxSize
}