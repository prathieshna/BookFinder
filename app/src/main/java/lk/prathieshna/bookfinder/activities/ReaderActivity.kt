package lk.prathieshna.bookfinder.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.squareup.picasso.Picasso
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.projections.getSelectedItemEmbeddedURL
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeAuthors
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeName
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeSubtitle
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeThumbnailImageURL
import lk.prathieshna.bookfinder.store.bookFinderStore


class ReaderActivity : BaseActivity() {
    private lateinit var adView: AdView
    private var initialLayoutComplete = false

    // View references
    private lateinit var webView: WebView
    private lateinit var adViewContainer: FrameLayout
    private lateinit var llBottomBar: android.widget.LinearLayout
    private lateinit var tvBookAuthor: TextView
    private lateinit var tvBookTitle: TextView
    private lateinit var tvBookSubtitle: TextView
    private lateinit var ivBookThumbnail: ImageView

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reader)

        supportActionBar?.hide()

        // Apply window insets for edge-to-edge
        val rootLayout = findViewById<android.view.View>(R.id.root_layout)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        // Initialize views
        webView = findViewById(R.id.webView)
        adViewContainer = findViewById(R.id.ad_view_container)
        llBottomBar = findViewById(R.id.ll_bottom_bar)
        tvBookAuthor = findViewById(R.id.tv_book_author)
        tvBookTitle = findViewById(R.id.tv_book_title)
        tvBookSubtitle = findViewById(R.id.tv_book_subtitle)
        ivBookThumbnail = findViewById(R.id.iv_book_thumbnail)

        llBottomBar.viewTreeObserver.addOnGlobalLayoutListener {
            if (llBottomBar.height > 0) {
                webView.setPadding(0, 0, 0, llBottomBar.height)
            }
        }

        showLoader()

        webView.settings.javaScriptEnabled = true
        webView.settings.loadWithOverviewMode = true
        webView.settings.useWideViewPort = false
        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            @Suppress("OVERRIDE_DEPRECATION")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                startActivity(intent)
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                hideLoader()
            }
        }
        webView.loadUrl(getSelectedItemEmbeddedURL(bookFinderStore.state, this))


        adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
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
        adView = AdView(this)
        adView.adUnitId = getString(R.string.ad_unit_id_reader_page)
        adView.setAdSize(adSize)
        adViewContainer.addView(adView)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private val adSize: AdSize
        get() {
            val displayMetrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                val windowMetrics = windowManager.currentWindowMetrics
                val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(
                    android.view.WindowInsets.Type.systemBars()
                )
                DisplayMetrics().apply {
                    val bounds = windowMetrics.bounds
                    widthPixels = bounds.width() - insets.left - insets.right
                    heightPixels = bounds.height() - insets.top - insets.bottom
                    density = resources.displayMetrics.density
                }
            } else {
                @Suppress("DEPRECATION")
                resources.displayMetrics
            }

            var adWidthPixels = adViewContainer.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = displayMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / displayMetrics.density).toInt()
            return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    override fun onResume() {
        super.onResume()
        if (::adView.isInitialized) {
            adView.resume()
        }
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        if (::adView.isInitialized) {
            adView.pause()
        }
        super.onPause()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        if (::adView.isInitialized) {
            adView.destroy()
        }
        super.onDestroy()
    }

    private fun setUpHeaders() {
        tvBookAuthor.text = getSelectedItemVolumeAuthors(bookFinderStore.state, this)
        tvBookTitle.text = getSelectedItemVolumeName(bookFinderStore.state, this)
        tvBookSubtitle.text = getSelectedItemVolumeSubtitle(bookFinderStore.state, this)
        Picasso.get().load(getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state))
            .into(ivBookThumbnail)
    }
}