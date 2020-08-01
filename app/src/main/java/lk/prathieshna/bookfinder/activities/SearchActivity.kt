package lk.prathieshna.bookfinder.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import kotlinx.android.synthetic.main.activity_search.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.adapters.BookSearchAdapter
import lk.prathieshna.bookfinder.domain.local.Item
import lk.prathieshna.bookfinder.middleware.utils.getLastVisibleItem
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.getTotalItems
import lk.prathieshna.bookfinder.state.getVolumes
import lk.prathieshna.bookfinder.store.bookFinderStore


class SearchActivity : BaseActivity() {

    private var searchResultItems: MutableList<Item> = mutableListOf()
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
                adapter.notifyDataSetChanged()
                tv_results_meta_data.text = getTotalItems(bookFinderStore.state, this)
                isLoading = false
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
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        setUpSearchButton()
        setUpSearchTextWatcher()
        setUpSearchResultsGrid()
    }

    private fun setUpSearchResultsGrid() {
        gridLayoutManager = StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL)
        rv_search_results.layoutManager = gridLayoutManager
        adapter = BookSearchAdapter(this, searchResultItems) {}
        rv_search_results.adapter = adapter

        rv_search_results.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val lastVisibleItemPosition: Int
                val lastVisibleItemPositions = gridLayoutManager.findLastVisibleItemPositions(null)
                lastVisibleItemPosition = getLastVisibleItem(lastVisibleItemPositions)
                if (!isLoading && lastVisibleItemPosition + 8 > gridLayoutManager.itemCount && searchResultItems.size > 0 && !isEOL) {
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
            }
        })
    }
}
