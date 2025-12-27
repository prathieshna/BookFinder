package lk.prathieshna.bookfinder.services

import com.google.gson.Gson
import lk.prathieshna.bookfinder.domain.api.OpenLibraryWork
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface OpenLibraryService {
    // Get work by Open Library work ID
    @GET("works/{work_id}.json")
    fun getWorkById(
        @Path("work_id") workId: String
    ): Call<OpenLibraryWork>
}

object OpenLibraryRetrofitClient {
    private const val BASE_URL = "https://openlibrary.org/"

    private var retrofit: Retrofit? = null

    val instance: Retrofit
        get() {
            if (retrofit == null) {
                val interceptor = HttpLoggingInterceptor()
                interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

                val okClient = OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(Gson()))
                    .client(okClient)
                    .build()
            }
            return retrofit as Retrofit
        }
}

fun openLibraryService(): OpenLibraryService {
    val retrofit = OpenLibraryRetrofitClient.instance
    return retrofit.create(OpenLibraryService::class.java)
}
