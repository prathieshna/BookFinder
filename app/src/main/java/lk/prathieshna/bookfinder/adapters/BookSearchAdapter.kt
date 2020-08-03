package lk.prathieshna.bookfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_search_result_item.view.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.state.projections.getVolumeAuthors
import lk.prathieshna.bookfinder.state.projections.getVolumeName
import lk.prathieshna.bookfinder.state.projections.getVolumeThumbnailImageURL
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
                UnifiedNativeAdViewHolder(unifiedNativeLayoutView)
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

        init {
            itemView.setOnClickListener {
                clickHandler(searchItem ?: Item())
            }
        }

        fun setData(item: Item?, position: Int) {
            this.searchItem = item
            this.itemIndex = position

            if (item?.id != null) {
                itemView.tv_book_title.text =
                    getVolumeName(bookFinderStore.state, item.id!!, context)
                itemView.tv_book_author.text =
                    getVolumeAuthors(bookFinderStore.state, item.id!!, context)
                Picasso.get().load(getVolumeThumbnailImageURL(bookFinderStore.state, item.id!!))
                    .into(itemView.iv_book_thumbnail)
                getDominantColorFromImageURL(
                    context,
                    getVolumeThumbnailImageURL(bookFinderStore.state, item.id!!)
                ) { dominantColor ->
                    itemView.tv_book_title.setTextColor(dominantColor)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        val recyclerViewItem: Any = data[position]
        return if (recyclerViewItem is UnifiedNativeAd) {
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
                val nativeAd = data[position] as UnifiedNativeAd
                populateNativeAdView(nativeAd, (holder as UnifiedNativeAdViewHolder).getAdView())
            }
            ITEM_VIEW_TYPE -> {
                val item = data[position] as Item
                holder as BookSearchAdapter.ViewHolder
                holder.setData(item, position)
            }
            else -> {
                val item = data[position] as Item
                holder as BookSearchAdapter.ViewHolder
                holder.setData(item, position)
            }
        }


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
        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAd)
    }

}
