package lk.prathieshna.bookfinder.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.adapters.BookFavouriteAdapter
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.utils.DatabaseHandler


class FavouritesActivity : BaseActivity() {
    private var favouriteItems = mutableListOf<Any>()
    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var adapter: BookFavouriteAdapter

    // View references
    private lateinit var rvFavourites: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

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
        rvFavourites = findViewById(R.id.rv_favourites)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rvFavourites.layoutManager = linearLayoutManager

        databaseHandler = DatabaseHandler(this.applicationContext)
        favouriteItems = databaseHandler.getFavouriteBooks().toMutableList()
        favouriteItems.reverse()

        adapter = BookFavouriteAdapter(favouriteItems, { selectedItem ->
            dispatchAction(
                GetVolumeByID.Request(
                    id = selectedItem.id ?: "",
                    actionId = getActionId(),
                    context = this
                )
            )
        }, { selectedItem ->
            databaseHandler.removeFromFavouritesById(selectedItem.id!!)
            val position = favouriteItems.indexOf(selectedItem)
            favouriteItems.remove(selectedItem)
            if (position != -1) {
                adapter.notifyItemRemoved(position)
            }
        })
        rvFavourites.adapter = adapter
    }

    override fun onStateUpdate(state: UdfBaseState<AppState>, action: BaseAction): Boolean {
        return when (action) {
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

    override fun onResume() {
        super.onResume()
        mNativeAds.forEach { it.destroy() }
        mNativeAds.clear()
        favouriteItems.clear()
        favouriteItems.addAll(databaseHandler.getFavouriteBooks())
        favouriteItems.reverse()
        @Suppress("NotifyDataSetChanged")
        adapter.notifyDataSetChanged()
        loadNativeAds()
    }

    override fun onDestroy() {
        super.onDestroy()
        mNativeAds.forEach { it.destroy() }
        mNativeAds.clear()
    }

    override fun onRawStateUpdate(state: UdfBaseState<AppState>) {
    }

    override fun onError(action: BaseAction) {
        when (action) {
            is GetVolumeByID -> {
                alertDialog.showDialog(action.error?.message ?: getString(R.string.not_available))
            }
        }
    }

    companion object {
        private const val AD_INTERVAL = 10
    }

    private var adLoader: AdLoader? = null
    private val mNativeAds = mutableListOf<NativeAd>()

    private fun insertAdsInMenuItems() {
        if (mNativeAds.isEmpty()) return
        if (favouriteItems.size < AD_INTERVAL) {
            favouriteItems.add(mNativeAds[0])
            return
        }
        var index = AD_INTERVAL - 1
        for (ad in mNativeAds) {
            if (index < favouriteItems.size) {
                favouriteItems.add(index, ad)
                index += AD_INTERVAL + 1
            }
        }
    }

    private fun loadNativeAds() {
        val numberOfAds = maxOf(1, favouriteItems.size / AD_INTERVAL)
        val builder = AdLoader.Builder(this, getString(R.string.ad_unit_id_favourites))
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
                        Log.e("FavouritesActivity", "Native ad failed to load: ${loadAdError.message}")
                        if (!adLoader!!.isLoading) {
                            insertAdsInMenuItems()
                            @Suppress("NotifyDataSetChanged")
                            adapter.notifyDataSetChanged()
                        }
                    }
                }).build()

        adLoader?.loadAds(AdRequest.Builder().build(), numberOfAds)
    }
}