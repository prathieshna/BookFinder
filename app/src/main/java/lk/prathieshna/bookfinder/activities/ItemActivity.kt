package lk.prathieshna.bookfinder.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.projections.getSelectedItemId
import lk.prathieshna.bookfinder.state.projections.getSelectedItemViewability
import lk.prathieshna.bookfinder.state.projections.getSelectedItemViewabilityText
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeAuthors
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeDescription
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeName
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeRating
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeRatingCountString
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeSubtitle
import lk.prathieshna.bookfinder.state.projections.getSelectedItemVolumeThumbnailImageURL
import lk.prathieshna.bookfinder.store.bookFinderStore
import lk.prathieshna.bookfinder.utils.DatabaseHandler
import lk.prathieshna.bookfinder.utils.getDominantColorFromImageURL


class ItemActivity : BaseActivity() {
    private lateinit var adView: AdView
    private lateinit var adView2: AdView
    private var isFavourite = false
    private lateinit var databaseHandler: DatabaseHandler

    private var initialLayoutComplete = false
    private var initialLayoutComplete2 = false

    // View references
    private lateinit var adViewContainer: FrameLayout
    private lateinit var adViewContainer2: FrameLayout
    private lateinit var bPreview: Button
    private lateinit var bViewReviews: Button
    private lateinit var tvBookAuthor: TextView
    private lateinit var tvBookTitle: TextView
    private lateinit var tvBookSubtitle: TextView
    private lateinit var ivBookThumbnail: ImageView
    private lateinit var tvBookDescription: TextView
    private lateinit var rbStars: RatingBar
    private lateinit var tvReviews: TextView
    private lateinit var ivAddToFavourites: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        // Initialize views
        adViewContainer = findViewById(R.id.ad_view_container)
        adViewContainer2 = findViewById(R.id.ad_view_container_2)
        bPreview = findViewById(R.id.b_preview)
        bViewReviews = findViewById(R.id.b_view_reviews)
        tvBookAuthor = findViewById(R.id.tv_book_author)
        tvBookTitle = findViewById(R.id.tv_book_title)
        tvBookSubtitle = findViewById(R.id.tv_book_subtitle)
        ivBookThumbnail = findViewById(R.id.iv_book_thumbnail)
        tvBookDescription = findViewById(R.id.tv_book_description)
        rbStars = findViewById(R.id.rb_stars)
        tvReviews = findViewById(R.id.tv_reviews)
        ivAddToFavourites = findViewById(R.id.iv_add_to_favourites)

        databaseHandler = DatabaseHandler(this.applicationContext)
        checkFavouriteStatus(databaseHandler)
        setUpFab()
        animateFabColor()
        setUpHeaders()


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

        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        adViewContainer.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

        adViewContainer2.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete2) {
                initialLayoutComplete2 = true
                loadBanner2()
            }
        }

        bPreview.text = getSelectedItemViewabilityText(bookFinderStore.state, this)
        bPreview.isEnabled = getSelectedItemViewability(bookFinderStore.state)
        bPreview.setOnClickListener {
            val intent = Intent(this, ReaderActivity::class.java)
            startActivity(intent)
        }

        bViewReviews.setOnClickListener {
            val bookId = getSelectedItemId(bookFinderStore.state, this)
            val reviewsUrl = getString(R.string.google_books_reviews_url, bookId)
            val intent = Intent(Intent.ACTION_VIEW, reviewsUrl.toUri())
            startActivity(intent)
        }

    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setUpHeaders() {
        tvBookAuthor.text = getSelectedItemVolumeAuthors(bookFinderStore.state, this)
        tvBookTitle.text = getSelectedItemVolumeName(bookFinderStore.state, this)
        tvBookSubtitle.text = getSelectedItemVolumeSubtitle(bookFinderStore.state, this)
        Picasso.get().load(getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state))
            .into(ivBookThumbnail)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvBookDescription.text = Html.fromHtml(
                getSelectedItemVolumeDescription(bookFinderStore.state, this),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            tvBookDescription.text =
                Html.fromHtml(getSelectedItemVolumeDescription(bookFinderStore.state, this))
        }
        rbStars.rating = getSelectedItemVolumeRating(bookFinderStore.state)
        tvReviews.text = getSelectedItemVolumeRatingCountString(bookFinderStore.state, this)
    }

    private fun animateFabColor() {
        getDominantColorFromImageURL(
            this,
            getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state)
        ) { dominantColor ->
            val colorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.colorPrimary),
                dominantColor
            )
            colorAnimation.duration = 250 // milliseconds
            colorAnimation.addUpdateListener { animator ->
                tvBookDescription.setBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimation.start()
        }
    }

    private fun setUpFab() {
        ivAddToFavourites.setOnClickListener { view ->
            if (isFavourite) {
                ivAddToFavourites.setImageResource(R.drawable.ic_heart)
                databaseHandler.removeFromFavourites()
                isFavourite = false
                Snackbar.make(
                    view,
                    getString(R.string.fav_remove_sb_message),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo)) {
                        databaseHandler.addToFavourites()
                        isFavourite = true
                        ivAddToFavourites.setImageResource(R.drawable.ic_heart_tick)
                        Snackbar.make(
                            view,
                            getString(R.string.fav_added_sb_message),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.show()
            } else {
                ivAddToFavourites.setImageResource(R.drawable.ic_heart_tick)
                databaseHandler.addToFavourites()
                isFavourite = true
                Snackbar.make(view, getString(R.string.fav_added_sb_message), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        databaseHandler.removeFromFavourites()
                        isFavourite = false
                        ivAddToFavourites.setImageResource(R.drawable.ic_heart)
                        Snackbar.make(
                            view,
                            getString(R.string.fav_remove_sb_message),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkFavouriteStatus(databaseHandler)
        if (::adView.isInitialized) {
            adView.resume()
        }
        if (::adView2.isInitialized) {
            adView2.resume()
        }
    }

    private fun checkFavouriteStatus(databaseHandler: DatabaseHandler) {
        isFavourite = databaseHandler.getFavouriteStatus()
        if (isFavourite)
            ivAddToFavourites.setImageResource(R.drawable.ic_heart_tick)
        else
            ivAddToFavourites.setImageResource(R.drawable.ic_heart)
    }

    override fun onStateUpdate(state: UdfBaseState<AppState>, action: BaseAction): Boolean {
        return false
    }

    override fun onRawStateUpdate(state: UdfBaseState<AppState>) {
    }

    override fun onError(action: BaseAction) {
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

    private val adSize2: AdSize
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

            var adWidthPixels = adViewContainer2.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = displayMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / displayMetrics.density).toInt()
            return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    private fun loadBanner() {
        adView = AdView(this)
        adView.adUnitId = getString(R.string.ad_unit_id_detail_page_1)
        adView.setAdSize(adSize)
        adViewContainer.addView(adView)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    private fun loadBanner2() {
        adView2 = AdView(this)
        adView2.adUnitId = getString(R.string.ad_unit_id_detail_page_2)
        adView2.setAdSize(adSize2)
        adViewContainer2.addView(adView2)

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView2.loadAd(adRequest)
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        if (::adView.isInitialized) {
            adView.pause()
        }
        if (::adView2.isInitialized) {
            adView2.pause()
        }
        super.onPause()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        if (::adView.isInitialized) {
            adView.destroy()
        }
        if (::adView2.isInitialized) {
            adView2.destroy()
        }
        super.onDestroy()
    }
}
