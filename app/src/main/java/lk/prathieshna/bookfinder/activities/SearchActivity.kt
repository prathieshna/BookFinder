package lk.prathieshna.bookfinder.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.gms.ads.*
import com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_UNSPECIFIED
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import kotlinx.android.synthetic.main.activity_search.*
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
    private var consentForm: ConsentForm? = null

    // The AdLoader used to load ads.
    private var adLoader: AdLoader? = null

    // List of native ads that have been successfully loaded.
    private val mNativeAds = mutableListOf<UnifiedNativeAd>()

    private var searchResultItems = mutableListOf<Any>()
    private lateinit var adapter: BookSearchAdapter
    private lateinit var gridLayoutManager: StaggeredGridLayoutManager
    private var isLoading: Boolean = false
    private var isEOL: Boolean = false

    override fun onStateUpdate(state: UdfBaseState<AppState>, action: BaseAction): Boolean {
        return when (action) {
            is GetVolumesBySearch -> {
                isEOL = searchResultItems.size == getVolumes(state).size
                searchResultItems.clear()
                searchResultItems.addAll(getVolumes(state))
                tv_results_meta_data.text = getTotalItems(bookFinderStore.state, this)
                adapter.notifyDataSetChanged()
                isLoading = false
                loadNativeAds()
                rl_search_results.visibility = View.VISIBLE
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

        val params = ConsentRequestParameters.Builder().build()

        consentInformation = UserMessagingPlatform.getConsentInformation(this)
        consentInformation?.requestConsentInfoUpdate(
            this,
            params,
            {
                // The consent information state was updated.
                // You are now ready to check if a form is available.
                if (consentInformation?.isConsentFormAvailable == true) {
                    loadForm()
                }
            },
            {
                // Handle the error.
            })

        val conf = RequestConfiguration.Builder()
            .setMaxAdContentRating(
                MAX_AD_CONTENT_RATING_UNSPECIFIED
            )
            .build()

        MobileAds.setRequestConfiguration(conf)
        MobileAds.initialize(this)

        iv_favourites.setOnClickListener {
            val intent = Intent(this, FavouritesActivity::class.java)
            startActivity(intent)
        }

        iv_clear.setOnClickListener {
            et_search.text.clear()
            iv_clear.visibility = View.GONE
            v_separator.visibility = View.GONE
            b_search.visibility = View.GONE
            iv_favourites.visibility = View.VISIBLE
        }

        setUpSearchButton()
        setUpSearchTextWatcher()
        setUpSearchResultsGrid()

    }

    private fun setUpSearchResultsGrid() {
        gridLayoutManager = StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
        rv_search_results.layoutManager = gridLayoutManager
        adapter = BookSearchAdapter(this, searchResultItems) { selectedItem ->
            dispatchAction(
                GetVolumeByID.Request(
                    id = selectedItem.id ?: "",
                    actionId = getActionId(),
                    context = this
                )
            )
        }
        rv_search_results.adapter = adapter

        rv_search_results.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPosition: Int
                val lastVisibleItemPositions = gridLayoutManager.findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                if (!isLoading && lastVisibleItemPosition + 5 > gridLayoutManager.itemCount && searchResultItems.size > 0 && !isEOL) {
                    isLoading = true
                    dispatchAction(
                        GetVolumesBySearch.Request(
                            q = et_search.text.toString(),
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
        b_search.setOnClickListener {
            isLoading = true
            dispatchAction(
                GetVolumesBySearch.Request(
                    q = et_search.text.toString(),
                    startIndex = 0,
                    context = this,
                    actionId = getActionId()
                )
            )
        }
    }

    private fun setUpSearchTextWatcher() {
        et_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(currentText: Editable?) {

            }

            override fun beforeTextChanged(
                currentText: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {

            }

            override fun onTextChanged(
                currentText: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                tv_results_meta_data.text = ""
                searchResultItems.clear()
                adapter.notifyDataSetChanged()
                isEOL = false

                iv_clear.visibility = View.VISIBLE
                v_separator.visibility = View.VISIBLE
                b_search.visibility = View.VISIBLE
                iv_favourites.visibility = View.GONE
                rl_search_results.visibility = View.GONE
            }
        })
    }

    companion object {
        // The number of native ads to load and display.
        const val NUMBER_OF_ADS = 1
    }


    //AD MOB STUFF
    private fun insertAdsInMenuItems() {
        if (mNativeAds.isEmpty()) {
            return
        }
        val offset: Int = searchResultItems.size / mNativeAds.size + 1
        var index = 0
        for (ad in mNativeAds) {
            if (searchResultItems.size >= index) {
                searchResultItems.add(index, ad)
                index += offset
            }
        }
    }

    private fun loadNativeAds() {
        val builder = AdLoader.Builder(this, getString(R.string.ad_unit_id_search))
        adLoader =
            builder.forUnifiedNativeAd { unifiedNativeAd -> // A native ad loaded successfully, check if the ad loader has finished loading
                // and if so, insert the ads into the list.
                mNativeAds.add(unifiedNativeAd)
                if (!adLoader!!.isLoading) {
                    insertAdsInMenuItems()
                    adapter.notifyDataSetChanged()
                }
            }.withAdListener(
                object : AdListener() {
                    override fun onAdFailedToLoad(errorCode: Int) {
                        // A native ad failed to load, check if the ad loader has finished loading
                        // and if so, insert the ads into the list.
                        Log.e(
                            "MainActivity", "The previous native ad failed to load. Attempting to"
                                    + " load another."
                        )
                        if (!adLoader!!.isLoading) {
                            insertAdsInMenuItems()
                            adapter.notifyDataSetChanged()
                        }
                    }
                }).build()

        // Load the Native Express ad.
        adLoader?.loadAds(AdRequest.Builder().build(), NUMBER_OF_ADS)
    }


    private fun loadForm() {
        UserMessagingPlatform.loadConsentForm(
            this,
            { consentForm ->
                this.consentForm = consentForm
                if (consentInformation!!.consentStatus == ConsentInformation.ConsentStatus.REQUIRED) {
                    consentForm.show(
                        this
                    ) { // Handle dismissal by reloading form.
                        loadForm()
                    }
                }

            }
        ) {
            // Handle the error
        }
    }
}
