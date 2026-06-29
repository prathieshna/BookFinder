package lk.prathieshna.bookfinder.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.adapters.BookSearchAdapter
import lk.prathieshna.bookfinder.middleware.utils.getLastVisibleItem
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.projections.getTotalItems
import lk.prathieshna.bookfinder.state.projections.getVolumes
import lk.prathieshna.bookfinder.store.bookFinderStore


class SearchActivity : BaseActivity() {

    private var consentInformation: ConsentInformation? = null

    private var adLoader: AdLoader? = null
    private val mNativeAds = mutableListOf<NativeAd>()

    private var searchResultItems = mutableListOf<Any>()
    private lateinit var adapter: BookSearchAdapter
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager
    private var isLoading: Boolean = false
    private var isEOL: Boolean = false

    // View references
    private lateinit var ivFavourites: ImageView
    private lateinit var ivClear: ImageView
    private lateinit var vSeparator: View
    private lateinit var bSearch: Button
    private lateinit var etSearch: EditText
    private lateinit var tvResultsMetaData: TextView
    private lateinit var rlSearchResults: RelativeLayout
    private lateinit var rvSearchResults: RecyclerView

    override fun onStateUpdate(state: UdfBaseState<AppState>, action: BaseAction): Boolean {
        return when (action) {
            is GetVolumesBySearch -> {
                isEOL = searchResultItems.size == getVolumes(state).size
                searchResultItems.clear()
                searchResultItems.addAll(getVolumes(state))
                tvResultsMetaData.text = getTotalItems(bookFinderStore.state, this)
                @Suppress("NotifyDataSetChanged")
                adapter.notifyDataSetChanged()
                isLoading = false
                loadNativeAds()
                rlSearchResults.visibility = View.VISIBLE
                true
            }
            is GetVolumeByID -> {
                val intent = Intent(this, ItemActivity::class.java)
                startActivity(intent)
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onRawStateUpdate(state: UdfBaseState<AppState>) {
    }

    override fun onError(action: BaseAction) {
        when (action) {
            is GetVolumesBySearch -> {
                alertDialog.showDialog(action.error?.message ?: getString(R.string.not_available))
                isLoading = false
            }
            is GetVolumeByID -> {
                alertDialog.showDialog(action.error?.message ?: getString(R.string.not_available))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        supportActionBar?.hide()

        val rootLayout = findViewById<View>(R.id.root_layout)
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            val existingTopPadding = 20 * resources.displayMetrics.density.toInt()
            v.setPadding(
                systemBars.left,
                systemBars.top + existingTopPadding,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }

        ivFavourites = findViewById(R.id.iv_favourites)
        ivClear = findViewById(R.id.iv_clear)
        vSeparator = findViewById(R.id.v_separator)
        bSearch = findViewById(R.id.b_search)
        etSearch = findViewById(R.id.et_search)
        tvResultsMetaData = findViewById(R.id.tv_results_meta_data)
        rlSearchResults = findViewById(R.id.rl_search_results)
        rvSearchResults = findViewById(R.id.rv_search_results)

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        val params = ConsentRequestParameters.Builder().build()
        consentInformation?.requestConsentInfoUpdate(
            this,
            params,
            {
                if (consentInformation?.isConsentFormAvailable == true) {
                    loadForm()
                } else {
                    initializeMobileAds()
                }
            },
            { error ->
                Log.w("SearchActivity", "Consent update failed: ${error.message}")
                initializeMobileAds()
            }
        )

        ivFavourites.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }

        ivClear.setOnClickListener {
            etSearch.text.clear()
            ivClear.visibility = View.GONE
            vSeparator.visibility = View.GONE
            bSearch.visibility = View.GONE
            ivFavourites.visibility = View.VISIBLE
        }

        setUpSearchButton()
        setUpSearchTextWatcher()
        setUpSearchResultsGrid()
    }

    override fun onDestroy() {
        super.onDestroy()
        mNativeAds.forEach { it.destroy() }
        mNativeAds.clear()
    }

    private fun setUpSearchResultsGrid() {
        gridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        rvSearchResults.layoutManager = gridLayoutManager
        adapter = BookSearchAdapter(this, searchResultItems) { selectedItem ->
            dispatchAction(
                GetVolumeByID.Request(
                    id = selectedItem.id ?: "",
                    actionId = getActionId(),
                    context = this
                )
            )
        }
        rvSearchResults.adapter = adapter

        rvSearchResults.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPosition: Int
                val lastVisibleItemPositions = gridLayoutManager.findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                if (!isLoading && lastVisibleItemPosition + 5 > gridLayoutManager.itemCount && searchResultItems.size > 0 && !isEOL) {
                    isLoading = true
                    dispatchAction(
                        GetVolumesBySearch.Request(
                            q = etSearch.text.toString(),
                            startIndex = searchResultItems.size - 1,
                            context = this@SearchActivity,
                            actionId = getActionId()
                        )
                    )
                }
            }
        })
    }

    private fun setUpSearchButton() {
        bSearch.setOnClickListener {
            isLoading = true
            dispatchAction(
                GetVolumesBySearch.Request(
                    q = etSearch.text.toString(),
                    startIndex = 0,
                    context = this,
                    actionId = getActionId()
                )
            )
        }
    }

    private fun setUpSearchTextWatcher() {
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(currentText: Editable?) {}

            override fun beforeTextChanged(
                currentText: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {}

            override fun onTextChanged(
                currentText: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                tvResultsMetaData.text = ""
                val size = searchResultItems.size
                searchResultItems.clear()
                adapter.notifyItemRangeRemoved(0, size)
                isEOL = false

                ivClear.visibility = View.VISIBLE
                vSeparator.visibility = View.VISIBLE
                bSearch.visibility = View.VISIBLE
                ivFavourites.visibility = View.GONE
                rlSearchResults.visibility = View.GONE
            }
        })
    }

    companion object {
        private const val AD_INTERVAL = 30
        private var isMobileAdsInitialized = false
    }

    private fun insertAdsInMenuItems() {
        if (mNativeAds.isEmpty()) return
        var index = AD_INTERVAL - 1  // first ad after 30 book items
        for (ad in mNativeAds) {
            if (index < searchResultItems.size) {
                searchResultItems.add(index, ad)
                index += AD_INTERVAL + 1  // +1 accounts for the inserted ad shifting positions
            }
        }
    }

    private fun loadNativeAds() {
        mNativeAds.forEach { it.destroy() }
        mNativeAds.clear()

        val numberOfAds = maxOf(1, searchResultItems.size / AD_INTERVAL)
        val builder = AdLoader.Builder(this, getString(R.string.ad_unit_id_search))
        adLoader = builder.forNativeAd { nativeAd ->
                mNativeAds.add(nativeAd)
                if (!adLoader!!.isLoading) {
                    insertAdsInMenuItems()
                    @Suppress("NotifyDataSetChanged")
                    adapter.notifyDataSetChanged()
                }
            }.withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        Log.e("SearchActivity", "Native ad failed to load: ${loadAdError.message}")
                        if (!adLoader!!.isLoading) {
                            insertAdsInMenuItems()
                            @Suppress("NotifyDataSetChanged")
                            adapter.notifyDataSetChanged()
                        }
                    }
                }).build()

        adLoader?.loadAds(AdRequest.Builder().build(), numberOfAds)
    }

    private fun initializeMobileAds() {
        if (!isMobileAdsInitialized) {
            isMobileAdsInitialized = true
            MobileAds.initialize(this)
        }
    }

    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { consentForm ->
                if (consentInformation?.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(this) { formError ->
                        if (formError != null) {
                            Log.w("SearchActivity", "Consent form error: ${formError.message}")
                        }
                        initializeMobileAds()
                    }
                } else {
                    initializeMobileAds()
                }
            },
            { error ->
                Log.w("SearchActivity", "Consent form load failed: ${error.message}")
                initializeMobileAds()
            }
        )
    }

}
