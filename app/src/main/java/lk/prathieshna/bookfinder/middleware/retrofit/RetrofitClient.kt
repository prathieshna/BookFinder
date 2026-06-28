package lk.prathieshna.bookfinder.middleware.retrofit

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import com.google.gson.Gson
import lk.prathieshna.bookfinder.constant.Constants.Companion.GOOGLE_BOOKS_BASE_URL
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.security.MessageDigest

object RetrofitClient {
    private var retrofit: Retrofit? = null

    fun init(context: Context) {
        if (retrofit != null) return

        val sha1 = getSignatureSha1(context)

        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        val dispatcher = Dispatcher()
        dispatcher.maxRequests = 1
        val okClient = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .addHeader("X-Android-Package", context.packageName)
                    .addHeader("X-Android-Cert", sha1)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(loggingInterceptor)
            .dispatcher(dispatcher)
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(GOOGLE_BOOKS_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .client(okClient)
            .build()
    }

    val instance: Retrofit
        get() = retrofit ?: error("RetrofitClient not initialized. Call init(context) first.")

    @Suppress("DEPRECATION")
    private fun getSignatureSha1(context: Context): String {
        val signatures = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNING_CERTIFICATES
            )
            info.signingInfo?.apkContentsSigners ?: emptyArray()
        } else {
            val info = context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.GET_SIGNATURES
            )
            info.signatures ?: emptyArray()
        }
        val md = MessageDigest.getInstance("SHA1")
        val digest = md.digest(signatures[0].toByteArray())
        return digest.joinToString("") { "%02X".format(it) }
    }
}
