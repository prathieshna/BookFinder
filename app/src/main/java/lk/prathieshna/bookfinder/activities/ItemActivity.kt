package lk.prathieshna.bookfinder.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
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
import lk.prathieshna.bookfinder.domain.api.OpenLibraryWork
import lk.prathieshna.bookfinder.services.openLibraryService
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import lk.prathieshna.bookfinder.state.projections.getSelectedItemId
import lk.prathieshna.bookfinder.state.projections.getSelectedItemInfoLink
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
    private lateinit var bViewOnGoogleBooks: Button
    private lateinit var bViewOnOpenLibrary: Button
    private lateinit var tvBookAuthor: TextView
    private lateinit var tvBookTitle: TextView
    private lateinit var tvBookSubtitle: TextView
    private lateinit var ivBookThumbnail: ImageView
    private lateinit var tvBookDescription: TextView
    private lateinit var rbStars: RatingBar
    private lateinit var tvReviews: TextView
    private lateinit var ivAddToFavourites: ImageView

    // Metadata views
    private lateinit var tvMetadataHeader: TextView
    private lateinit var llMetadataContent: View
    private lateinit var tvPublisherInfo: TextView
    private lateinit var tvPageCount: TextView
    private lateinit var tvCategories: TextView
    private lateinit var tvIsbnInfo: TextView

    // Open Library stats views
    private lateinit var tvOlStatsHeader: TextView
    private lateinit var llOlStatsContent: View
    private lateinit var tvOlRating: TextView
    private lateinit var tvOlWantToRead: TextView
    private lateinit var tvOlCurrentlyReading: TextView
    private lateinit var tvOlAlreadyRead: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)

        // Initialize views
        adViewContainer = findViewById(R.id.ad_view_container)
        adViewContainer2 = findViewById(R.id.ad_view_container_2)
        bPreview = findViewById(R.id.b_preview)
        bViewOnGoogleBooks = findViewById(R.id.b_view_on_google_books)
        bViewOnOpenLibrary = findViewById(R.id.b_view_on_open_library)
        tvBookAuthor = findViewById(R.id.tv_book_author)
        tvBookTitle = findViewById(R.id.tv_book_title)
        tvBookSubtitle = findViewById(R.id.tv_book_subtitle)
        ivBookThumbnail = findViewById(R.id.iv_book_thumbnail)
        tvBookDescription = findViewById(R.id.tv_book_description)
        rbStars = findViewById(R.id.rb_stars)
        tvReviews = findViewById(R.id.tv_reviews)
        ivAddToFavourites = findViewById(R.id.iv_add_to_favourites)

        // Initialize metadata views
        tvMetadataHeader = findViewById(R.id.tv_metadata_header)
        llMetadataContent = findViewById(R.id.ll_metadata_content)
        tvPublisherInfo = findViewById(R.id.tv_publisher_info)
        tvPageCount = findViewById(R.id.tv_page_count)
        tvCategories = findViewById(R.id.tv_categories)
        tvIsbnInfo = findViewById(R.id.tv_isbn_info)

        // Initialize Open Library stats views
        tvOlStatsHeader = findViewById(R.id.tv_ol_stats_header)
        llOlStatsContent = findViewById(R.id.ll_ol_stats_content)
        tvOlRating = findViewById(R.id.tv_ol_rating)
        tvOlWantToRead = findViewById(R.id.tv_ol_want_to_read)
        tvOlCurrentlyReading = findViewById(R.id.tv_ol_currently_reading)
        tvOlAlreadyRead = findViewById(R.id.tv_ol_already_read)

        databaseHandler = DatabaseHandler(this.applicationContext)
        checkFavouriteStatus(databaseHandler)
        setUpFab()
        animateFabColor()
        setUpHeaders()
        setUpMetadata()
        setUpExternalLinks()
        loadOpenLibraryData()


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
    }

    private fun setUpMetadata() {
        val volumeInfo = bookFinderStore.state.state.selectedItem?.volumeInfo

        var hasMetadata = false

        // Publisher and published date
        val publisher = volumeInfo?.publisher
        val publishedDate = volumeInfo?.publishedDate
        if (!publisher.isNullOrBlank() || !publishedDate.isNullOrBlank()) {
            val publisherText = when {
                !publisher.isNullOrBlank() && !publishedDate.isNullOrBlank() ->
                    getString(R.string.publisher_info, publisher, publishedDate)
                !publisher.isNullOrBlank() ->
                    getString(R.string.publisher_only, publisher)
                else ->
                    getString(R.string.publisher_only, publishedDate ?: "")
            }
            tvPublisherInfo.text = publisherText
            tvPublisherInfo.visibility = View.VISIBLE
            hasMetadata = true
        } else {
            tvPublisherInfo.visibility = View.GONE
        }

        // Page count
        val pageCount = volumeInfo?.pageCount
        if (pageCount != null && pageCount > 0) {
            tvPageCount.text = getString(R.string.page_count, pageCount)
            tvPageCount.visibility = View.VISIBLE
            hasMetadata = true
        } else {
            tvPageCount.visibility = View.GONE
        }

        // Categories
        val categories = volumeInfo?.categories
        if (!categories.isNullOrEmpty()) {
            val categoriesText = categories.filterNotNull().take(3).joinToString(", ")
            if (categoriesText.isNotBlank()) {
                tvCategories.text = getString(R.string.categories, categoriesText)
                tvCategories.visibility = View.VISIBLE
                hasMetadata = true
            } else {
                tvCategories.visibility = View.GONE
            }
        } else {
            tvCategories.visibility = View.GONE
        }

        // ISBN
        val isbn = volumeInfo?.industryIdentifiers?.firstOrNull {
            it?.type == "ISBN_13" || it?.type == "ISBN_10"
        }
        if (isbn != null && !isbn.identifier.isNullOrBlank()) {
            tvIsbnInfo.text = getString(R.string.isbn_info, isbn.type ?: "", isbn.identifier ?: "")
            tvIsbnInfo.visibility = View.VISIBLE
            hasMetadata = true
        } else {
            tvIsbnInfo.visibility = View.GONE
        }

        // Show/hide metadata section
        if (hasMetadata) {
            tvMetadataHeader.visibility = View.VISIBLE
            llMetadataContent.visibility = View.VISIBLE
        } else {
            tvMetadataHeader.visibility = View.GONE
            llMetadataContent.visibility = View.GONE
        }
    }

    private fun setUpExternalLinks() {
        // Google Books button
        bViewOnGoogleBooks.setOnClickListener {
            val infoLink = getSelectedItemInfoLink(bookFinderStore.state)
            val url = if (!infoLink.isNullOrBlank()) {
                infoLink
            } else {
                val bookId = getSelectedItemId(bookFinderStore.state, this)
                "https://books.google.com/books?id=$bookId"
            }
            val intent = Intent(Intent.ACTION_VIEW, url.toUri())
            startActivity(intent)
        }

        // Open Library button - will be enabled if we have ISBN
        val volumeInfo = bookFinderStore.state.state.selectedItem?.volumeInfo
        val isbn = volumeInfo?.industryIdentifiers?.firstOrNull {
            it?.type == "ISBN_13" || it?.type == "ISBN_10"
        }?.identifier

        if (!isbn.isNullOrBlank()) {
            bViewOnOpenLibrary.isEnabled = true
            bViewOnOpenLibrary.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, "https://openlibrary.org/isbn/$isbn".toUri())
                startActivity(intent)
            }
        } else {
            bViewOnOpenLibrary.isEnabled = false
        }
    }

    private fun loadOpenLibraryData() {
        // Try to get ISBN from the selected item
        val volumeInfo = bookFinderStore.state.state.selectedItem?.volumeInfo
        val isbn = volumeInfo?.industryIdentifiers?.firstOrNull {
            it?.type == "ISBN_13" || it?.type == "ISBN_10"
        }?.identifier

        if (!isbn.isNullOrBlank()) {
            // Fetch Open Library data using ISBN
            fetchOpenLibraryWorkByISBN(isbn)
        } else {
            Log.d("ItemActivity", "No ISBN found for this book")
        }
    }

    private fun fetchOpenLibraryWorkByISBN(isbn: String) {
        // Open Library ISBN API returns book data with work key
        val call = openLibraryService().getWorkById("ISBN:$isbn")

        call.enqueue(object : Callback<OpenLibraryWork> {
            override fun onResponse(call: Call<OpenLibraryWork>, response: Response<OpenLibraryWork>) {
                if (response.isSuccessful) {
                    val work = response.body()
                    work?.let {
                        displayOpenLibraryStats(it)
                    }
                } else {
                    Log.d("ItemActivity", "Open Library API error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<OpenLibraryWork>, t: Throwable) {
                Log.e("ItemActivity", "Open Library API failure", t)
            }
        })
    }

    private fun displayOpenLibraryStats(work: OpenLibraryWork) {
        var hasStats = false

        // Rating
        val rating = work.ratingsAverage
        val ratingCount = work.ratingsCount
        if (rating != null && rating > 0 && ratingCount != null && ratingCount > 0) {
            tvOlRating.text = getString(R.string.ol_rating, rating, ratingCount)
            tvOlRating.visibility = View.VISIBLE
            hasStats = true
        } else {
            tvOlRating.visibility = View.GONE
        }

        // Want to read count
        val wantToRead = work.wantToReadCount
        if (wantToRead != null && wantToRead > 0) {
            tvOlWantToRead.text = getString(R.string.ol_want_to_read, wantToRead)
            tvOlWantToRead.visibility = View.VISIBLE
            hasStats = true
        } else {
            tvOlWantToRead.visibility = View.GONE
        }

        // Currently reading count
        val currentlyReading = work.currentlyReadingCount
        if (currentlyReading != null && currentlyReading > 0) {
            tvOlCurrentlyReading.text = getString(R.string.ol_currently_reading, currentlyReading)
            tvOlCurrentlyReading.visibility = View.VISIBLE
            hasStats = true
        } else {
            tvOlCurrentlyReading.visibility = View.GONE
        }

        // Already read count
        val alreadyRead = work.alreadyReadCount
        if (alreadyRead != null && alreadyRead > 0) {
            tvOlAlreadyRead.text = getString(R.string.ol_already_read, alreadyRead)
            tvOlAlreadyRead.visibility = View.VISIBLE
            hasStats = true
        } else {
            tvOlAlreadyRead.visibility = View.GONE
        }

        // Show/hide stats section
        if (hasStats) {
            tvOlStatsHeader.visibility = View.VISIBLE
            llOlStatsContent.visibility = View.VISIBLE
        } else {
            tvOlStatsHeader.visibility = View.GONE
            llOlStatsContent.visibility = View.GONE
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
