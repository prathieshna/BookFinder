package lk.prathieshna.bookfinder

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import lk.prathieshna.bookfinder.middleware.retrofit.RetrofitClient

class BookFinderApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
        val conf = RequestConfiguration.Builder()
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build()
        MobileAds.setRequestConfiguration(conf)
        MobileAds.initialize(this)
    }
}
