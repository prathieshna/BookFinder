package lk.prathieshna.bookfinder.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import lk.prathieshna.bookfinder.R


class UnifiedNativeAdViewHolder internal constructor(view: View) :
    RecyclerView.ViewHolder(view) {
    private val adView: UnifiedNativeAdView =
        view.findViewById<View>(R.id.ad_view) as UnifiedNativeAdView

    init {
        adView.mediaView = adView.findViewById<View>(R.id.ad_media) as MediaView
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_icon)
    }

    fun getAdView(): UnifiedNativeAdView {
        return adView
    }
}