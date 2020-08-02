package lk.prathieshna.bookfinder.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_favourites.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.GetVolumeByID
import lk.prathieshna.bookfinder.adapters.BookFavouriteAdapter
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.utils.DatabaseHandler

class FavouritesActivity : BaseActivity() {
    private var favouriteItems: MutableList<Item> = mutableListOf()
    lateinit var databaseHandler: DatabaseHandler
    lateinit var adapter: BookFavouriteAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites)

        val linearLayoutManager = LinearLayoutManager(this)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_favourites.layoutManager = linearLayoutManager

        databaseHandler = DatabaseHandler(this.applicationContext)
        favouriteItems = databaseHandler.getFavouriteBooks().toMutableList()
        favouriteItems.reverse()

        adapter = BookFavouriteAdapter(this, favouriteItems) { selectedItem ->
            dispatchAction(
                GetVolumeByID.Request(
                    id = selectedItem.id ?: "",
                    actionId = getActionId(),
                    context = this
                )
            )
        }
        rv_favourites.adapter = adapter
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
        favouriteItems.clear()
        favouriteItems.addAll(databaseHandler.getFavouriteBooks())
        favouriteItems.reverse()
        adapter.notifyDataSetChanged()
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
}