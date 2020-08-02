package lk.prathieshna.bookfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_favourite_item.view.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.utils.getDominantColorFromImageURL


class BookFavouriteAdapter(
    private val context: Context,
    val data: List<Item>,
    val clickHandler: (Item) -> Unit
) : RecyclerView.Adapter<BookFavouriteAdapter.ViewHolder>() {

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.layout_favourite_item, p0, false)
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
        private var favouriteItem: Item? = null
        private var itemIndex: Int = 0

        init {
            itemView.setOnClickListener {
                clickHandler(favouriteItem ?: Item())
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
            getDominantColorFromImageURL(
                context,
                favouriteItem?.volumeInfo?.imageLinks?.thumbnail!!
            ) { dominantColor ->
                itemView.tv_book_title.setTextColor(dominantColor)
            }
        }
    }
}
