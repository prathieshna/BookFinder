package lk.prathieshna.bookfinder.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import androidx.core.content.ContextCompat
import com.google.android.gms.ads.*
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.content_scrolling.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.projections.*
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
    // Determine the screen width (less decorations) to use for the ad width.
    // If the ad hasn't been laid out, default to the full screen width.

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)


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

        adView = AdView(this)
        adView2 = AdView(this)
        ad_view_container.addView(adView)
        ad_view_container_2.addView(adView2)
        // Since we're loading the banner based on the adContainerView size, we need to wait until this
        // view is laid out before we can get the width.
        ad_view_container.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete) {
                initialLayoutComplete = true
                loadBanner()
            }
        }

        ad_view_container_2.viewTreeObserver.addOnGlobalLayoutListener {
            if (!initialLayoutComplete2) {
                initialLayoutComplete2 = true
                loadBanner2()
            }
        }

        b_preview.text = getSelectedItemViewabilityText(bookFinderStore.state, this)
        b_preview.isEnabled = getSelectedItemViewability(bookFinderStore.state)
        b_preview.setOnClickListener {
            val intent = Intent(this, ReaderActivity::class.java)
            startActivity(intent)
        }

    }

    private fun setUpHeaders() {
        tv_book_author.text = getSelectedItemVolumeAuthors(bookFinderStore.state, this)
        tv_book_title.text = getSelectedItemVolumeName(bookFinderStore.state, this)
        tv_book_subtitle.text = getSelectedItemVolumeSubtitle(bookFinderStore.state, this)
        Picasso.get().load(getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state))
            .into(iv_book_thumbnail)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_book_description.text = Html.fromHtml(
                getSelectedItemVolumeDescription(bookFinderStore.state, this),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            tv_book_description.text =
                Html.fromHtml(getSelectedItemVolumeDescription(bookFinderStore.state, this))
        }
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
                nsv_description_root.setBackgroundColor(animator.animatedValue as Int)
            }
            colorAnimation.start()
        }
    }

    private fun setUpFab() {
        iv_add_to_favourites.setOnClickListener { view ->
            if (isFavourite) {
                iv_add_to_favourites.setImageResource(R.drawable.ic_heart)
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
                        iv_add_to_favourites.setImageResource(R.drawable.ic_heart_tick)
                        Snackbar.make(
                            view,
                            getString(R.string.fav_added_sb_message),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.show()
            } else {
                iv_add_to_favourites.setImageResource(R.drawable.ic_heart_tick)
                databaseHandler.addToFavourites()
                isFavourite = true
                Snackbar.make(view, getString(R.string.fav_added_sb_message), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        databaseHandler.removeFromFavourites()
                        isFavourite = false
                        iv_add_to_favourites.setImageResource(R.drawable.ic_heart)
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
        adView.resume()
        adView2.resume()
    }

    private fun checkFavouriteStatus(databaseHandler: DatabaseHandler) {
        isFavourite = databaseHandler.getFavouriteStatus()
        if (isFavourite)
            iv_add_to_favourites.setImageResource(R.drawable.ic_heart_tick)
        else
            iv_add_to_favourites.setImageResource(R.drawable.ic_heart)
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

    private val adSize2: AdSize
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

            var adWidthPixels = ad_view_container_2.width.toFloat()
            if (adWidthPixels == 0f) {
                adWidthPixels = outMetrics.widthPixels.toFloat()
            }

            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getPortraitAnchoredAdaptiveBannerAdSize(this, adWidth)
        }


    private fun loadBanner() {
        adView.adUnitId = getString(R.string.ad_unit_id_detail_page_1)

        adView.adSize = adSize

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView.loadAd(adRequest)
    }

    private fun loadBanner2() {
        adView2.adUnitId = getString(R.string.ad_unit_id_detail_page_2)

        adView2.adSize = adSize2

        // Create an ad request.
        val adRequest = AdRequest.Builder().build()

        // Start loading the ad in the background.
        adView2.loadAd(adRequest)
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        adView.pause()
        adView2.pause()
        super.onPause()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        adView.destroy()
        adView2.destroy()
        super.onDestroy()
    }
}