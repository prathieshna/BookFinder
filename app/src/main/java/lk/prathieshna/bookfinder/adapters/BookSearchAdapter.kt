package lk.prathieshna.bookfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.squareup.picasso.Picasso
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.state.projections.*
import lk.prathieshna.bookfinder.store.bookFinderStore
import lk.prathieshna.bookfinder.utils.getDominantColorFromImageURL


class BookSearchAdapter(
    private val context: Context,
    val data: List<Any>,
    val clickHandler: (Item) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val unifiedNativeLayoutView: View = LayoutInflater.from(
                    viewGroup.context
                ).inflate(
                    R.layout.ad_unified_search,
                    viewGroup, false
                )
                UnifiedNativeAdViewHolderSearch(unifiedNativeLayoutView)
            }
            ITEM_VIEW_TYPE -> {
                val menuItemLayoutView: View =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_search_result_item, viewGroup, false)
                ViewHolder(menuItemLayoutView)
            }
            else -> {
                val menuItemLayoutView: View =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.layout_search_result_item, viewGroup, false)
                ViewHolder(menuItemLayoutView)
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var searchItem: Item? = null
        private var itemIndex: Int = 0

        private val tvBookTitle: TextView = itemView.findViewById(R.id.tv_book_title)
        private val rbStars: RatingBar = itemView.findViewById(R.id.rb_stars)
        private val tvReviews: TextView = itemView.findViewById(R.id.tv_reviews)
        private val tvBookAuthor: TextView = itemView.findViewById(R.id.tv_book_author)
        private val ivBookThumbnail: ImageView = itemView.findViewById(R.id.iv_book_thumbnail)
        private val llSearchItem: LinearLayout = itemView.findViewById(R.id.ll_search_item)

        init {
            itemView.setOnClickListener {
                clickHandler(searchItem ?: Item())
            }
        }

        fun setData(item: Item?, position: Int) {
            this.searchItem = item
            this.itemIndex = position

            if (item?.id != null) {
                tvBookTitle.text =
                    getVolumeName(bookFinderStore.state, item.id!!, context)

                rbStars.rating = getVolumeRating(bookFinderStore.state, item.id!!)
                tvReviews.text =
                    getVolumeRatingCountString(bookFinderStore.state, item.id!!, context)

                tvBookAuthor.text =
                    getVolumeAuthors(bookFinderStore.state, item.id!!, context)
                Picasso.get().load(getVolumeThumbnailImageURL(bookFinderStore.state, item.id!!))
                    .into(ivBookThumbnail)
                getDominantColorFromImageURL(
                    context,
                    getVolumeThumbnailImageURL(bookFinderStore.state, item.id!!)
                ) { dominantColor ->
                    llSearchItem.setBackgroundColor(dominantColor)
//                    itemView.rl_search_item.background.alpha = 255
//                    llSearchItem.setBackgroundColor(dominantColor)
//                    llSearchItem.background.alpha = 255
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val recyclerViewItem: Any = data[position]
        return if (recyclerViewItem is NativeAd) {
            UNIFIED_NATIVE_AD_VIEW_TYPE
        } else ITEM_VIEW_TYPE
    }

    companion object {
        // A menu item view type.
        private const val ITEM_VIEW_TYPE = 0
        private const val UNIFIED_NATIVE_AD_VIEW_TYPE = 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            UNIFIED_NATIVE_AD_VIEW_TYPE -> {
                val nativeAd = data[position] as NativeAd
                populateNativeAdView(
                    nativeAd,
                    (holder as UnifiedNativeAdViewHolderSearch).getAdView()
                )
            }
            ITEM_VIEW_TYPE -> {
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

    private fun populateNativeAdView(
        nativeAd: NativeAd,
        adView: NativeAdView
    ) {
        // Some assets are guaranteed to be in every NativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every NativeAd, so it's important to
        // check before trying to display them.
        val icon = nativeAd.icon
        if (icon == null) {
            adView.iconView?.visibility = View.INVISIBLE
        } else {
            (adView.iconView as ImageView).setImageDrawable(icon.drawable)
            adView.iconView?.visibility = View.VISIBLE
        }
        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }

}
