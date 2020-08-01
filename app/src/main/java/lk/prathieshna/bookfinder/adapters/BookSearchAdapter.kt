package lk.prathieshna.bookfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_search_result_item.view.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.state.getVolumeAuthors
import lk.prathieshna.bookfinder.state.getVolumeName
import lk.prathieshna.bookfinder.state.getVolumeThumbnailImageURL
import lk.prathieshna.bookfinder.store.appStore
import lk.prathieshna.bookfinder.utils.getDominantColorFromImageURL


class BookSearchAdapter(
    private val context: Context,
    val data: List<Item>,
    val clickHandler: (Item?) -> Unit
) : RecyclerView.Adapter<BookSearchAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_search_result_item, p0, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, p1: Int) {
        val item = data[p1]
        holder.setData(item, p1)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var searchItem: Item? = null
        private var itemIndex: Int = 0

        init {
            itemView.setOnClickListener {
                clickHandler(searchItem)
            }
        }

        fun setData(item: Item?, position: Int) {
            this.searchItem = item
            this.itemIndex = position

            itemView.tv_book_title.text = getVolumeName(appStore.state, position, context)
            itemView.tv_book_author.text = getVolumeAuthors(appStore.state, position, context)
            Picasso.get().load(getVolumeThumbnailImageURL(appStore.state, position))
                .into(itemView.iv_book_thumbnail)
            getDominantColorFromImageURL(
                context,
                getVolumeThumbnailImageURL(appStore.state, position)
            ) { dominantColor ->
                itemView.tv_book_title.setTextColor(dominantColor)
            }
        }
    }

}
