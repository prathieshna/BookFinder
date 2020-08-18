package lk.prathieshna.bookfinder.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_favourite_item.view.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.local.Item


class BookFavouriteAdapter(
    val data: List<Any>,
    val clickHandler: (Item) -> Unit,
    val removeClickHandler: (Item) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView: View = LayoutInflater.from(
                    viewGroup.context
                ).inflate(
                    R.layout.ad_unified_favourite,
                    viewGroup, false
                )
                UnifiedNativeAdViewHolderFavourites(unifiedNativeLayoutView)
            }
            FAVOURITE_ITEM_VIEW_TYPE -> {
                val menuItemLayoutView: View =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_favourite_item, viewGroup, false)
                ViewHolder(menuItemLayoutView)
            }
            else -> {
                val menuItemLayoutView: View =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_favourite_item, viewGroup, false)
                ViewHolder(menuItemLayoutView)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val recyclerViewItem: Any = data[position]
        return if (recyclerViewItem is UnifiedNativeAd) {
            UNIFIED_NATIVE_AD_VIEW_TYPE
        } else FAVOURITE_ITEM_VIEW_TYPE
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val nativeAd = data[position] as UnifiedNativeAd
                populateNativeAdView(
                    nativeAd,
                    (holder as UnifiedNativeAdViewHolderFavourites).getAdView()
                )
            }
            FAVOURITE_ITEM_VIEW_TYPE -> {
                val item = data[position] as Item
                holder as ViewHolder
                holder.setData(item, position)
            }
            else -> {
                val item = data[position] as Item
                holder as ViewHolder
                holder.setData(item, position)
            }
        }


    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var favouriteItem: Item? = null
        private var itemIndex: Int = 0

        init {
            itemView.setOnClickListener {
                clickHandler(favouriteItem ?: Item())
            }

            itemView.b_remove.setOnClickListener {
                removeClickHandler(favouriteItem ?: Item())
            }
        }

        fun setData(item: Item?, position: Int) {
            this.favouriteItem = item
            this.itemIndex = position

            itemView.tv_book_title.text = favouriteItem?.volumeInfo?.title
            itemView.tv_book_subtitle.text = favouriteItem?.volumeInfo?.subtitle
            itemView.tv_book_author.text = favouriteItem?.volumeInfo?.authors?.get(0)
            Picasso.get().load(favouriteItem?.volumeInfo?.imageLinks?.thumbnail)
                .into(itemView.iv_book_thumbnail)
        }
    }

    companion object {
        // A menu item view type.
        private const val FAVOURITE_ITEM_VIEW_TYPE = 0

        // The unified native ad view type.
        private const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1
    }

    private fun populateNativeAdView(
        nativeAd: UnifiedNativeAd,
        adView: UnifiedNativeAdView
    ) {
        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
        val icon = nativeAd.icon
        if (icon == null) {
            adView.iconView.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }
        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            adView.priceView.visibility = View.VISIBLE
            (adView.priceView as TextView).text = nativeAd.price
        }
        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            adView.storeView.visibility = View.VISIBLE
            (adView.storeView as TextView).text = nativeAd.store
        }
        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }
        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }
}
