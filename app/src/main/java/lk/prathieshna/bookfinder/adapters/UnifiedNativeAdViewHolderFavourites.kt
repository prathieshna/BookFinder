package lk.prathieshna.bookfinder.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView
import lk.prathieshna.bookfinder.R


class UnifiedNativeAdViewHolderFavourites internal constructor(view: View) :
    RecyclerView.ViewHolder(view) {
    private val adView: NativeAdView =
        view.findViewById<View>(R.id.ad_view_favourite) as NativeAdView

    init {
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)
    }

    fun getAdView(): NativeAdView {
        return adView
    }
}