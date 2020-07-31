package lk.prathieshna.bookfinder.middleware.retrofit

import com.google.gson.Gson
import lk.prathieshna.bookfinder.constant.Constants.Companion.GOOGLE_BOOKS_BASE_URL
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var retrofit: Retrofit? = null
    val instance: Retrofit
        get() {
            if (retrofit == null) {

                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                val dispatcher = Dispatcher()
                dispatcher.maxRequests = 1
                val okClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .dispatcher(dispatcher)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(GOOGLE_BOOKS_BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(Gson()))
                    .client(okClient)
                    .build()
            }
            return retrofit as Retrofit
        }
}

