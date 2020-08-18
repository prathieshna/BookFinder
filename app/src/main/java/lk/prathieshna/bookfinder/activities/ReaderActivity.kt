package lk.prathieshna.bookfinder.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.webkit.WebView
import android.webkit.WebViewClient
import com.google.android.gms.ads.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_reader.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.projections.*
import lk.prathieshna.bookfinder.store.bookFinderStore


class ReaderActivity : BaseActivity() {
    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        supportActionBar?.hide()

        showLoader()

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = false
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                startActivity(intent)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideLoader()
            }
        }
        webView.loadUrl(getSelectedItemEmbeddedURL(bookFinderStore.state, this))


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) { }

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .build()
        )

        adView = AdView(this)
        ad_view_container.addView(adView)
        ad_view_container.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

        setUpHeaders()
    }

    override fun onStateUpdate(state: UdfBaseState<AppState>, action: BaseAction): Boolean {
        return false
    }

    override fun onRawStateUpdate(state: UdfBaseState<AppState>) {

    }

    override fun onError(action: BaseAction) {

    }

    private fun loadBanner() {
        adView.adUnitId = getString(R.string.ad_unit_id_reader_page)
        adView.adSize = adSize
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private val adSize: AdSize
        get() {
            @Suppress("DEPRECATION") val display =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    this.display
                } else {
                    windowManager.defaultDisplay
                }

            val outMetrics = DisplayMetrics()
            display?.getMetrics(outMetrics)

            val density = outMetrics.density

            var adWidthPixels = ad_view_container.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        adView.pause()
        super.onPause()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        adView.destroy()
        super.onDestroy()
    }

    private fun setUpHeaders() {
        tv_book_author.text = getSelectedItemVolumeAuthors(bookFinderStore.state, this)
        tv_book_title.text = getSelectedItemVolumeName(bookFinderStore.state, this)
        tv_book_subtitle.text = getSelectedItemVolumeSubtitle(bookFinderStore.state, this)
        Picasso.get().load(getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state))
            .into(iv_book_thumbnail)
    }
}