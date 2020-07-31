package lk.prathieshna.bookfinder.middleware.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.local.ApiError
import okhttp3.ResponseBody

fun parseError(errorResponse: ResponseBody?, code: Int, context: Context): ApiError? {
    if (code == 401) return ApiError(401.toString(), context.getString(R.string.unauthorized))
    val gSon = Gson()
    val type = object : TypeToken<ApiError>() {}.type

    return gSon.fromJson(errorResponse?.charStream(), type)
}
