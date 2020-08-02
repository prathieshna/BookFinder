package lk.prathieshna.bookfinder.activities

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_item.*
import kotlinx.android.synthetic.main.content_scrolling.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.state.projections.*
import lk.prathieshna.bookfinder.store.bookFinderStore
import lk.prathieshna.bookfinder.utils.DatabaseHandler
import lk.prathieshna.bookfinder.utils.darkenColour
import lk.prathieshna.bookfinder.utils.getDominantColorFromImageURL

class ItemActivity : BaseActivity() {
    private var isFavourite = false
    private lateinit var databaseHandler: DatabaseHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_item)
        setSupportActionBar(toolbar)
        toolbar_layout.title = getSelectedItemVolumeName(bookFinderStore.state, this)

        databaseHandler = DatabaseHandler(this.applicationContext)
        checkFavouriteStatus(databaseHandler)
        setUpFab()
        animateHeaderColor()
        setUpHeaders()
    }

    private fun setUpHeaders() {
        tv_book_author.text = getSelectedItemVolumeAuthors(bookFinderStore.state, this)
        tv_book_title.text = getSelectedItemVolumeName(bookFinderStore.state, this)
        tv_book_subtitle.text = getSelectedItemVolumeSubtitle(bookFinderStore.state, this)
        Picasso.get().load(getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state))
            .into(iv_book_thumbnail)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv_book_description.text = Html.fromHtml(
                getSelectedItemVolumeDescription(bookFinderStore.state, this),
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("DEPRECATION")
            tv_book_description.text =
                Html.fromHtml(getSelectedItemVolumeDescription(bookFinderStore.state, this))
        }
    }

    private fun animateHeaderColor() {
        getDominantColorFromImageURL(
            this,
            getSelectedItemVolumeThumbnailImageURL(bookFinderStore.state)
        ) { dominantColor ->
            var colorAnimation = ValueAnimator.ofObject(
                ArgbEvaluator(),
                ContextCompat.getColor(this, R.color.colorPrimary),
                dominantColor
            )
            colorAnimation.duration = 250 // milliseconds
            colorAnimation.addUpdateListener { animator ->
                toolbar_layout.setBackgroundColor(animator.animatedValue as Int)
                toolbar_layout.setContentScrimColor(animator.animatedValue as Int)
                toolbar_layout.setStatusBarScrimColor(animator.animatedValue as Int)
            }
            colorAnimation.start()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                colorAnimation = ValueAnimator.ofObject(
                    ArgbEvaluator(),
                    ContextCompat.getColor(this, R.color.colorPrimary),
                    darkenColour(dominantColor, 0.6f)
                )
                colorAnimation.duration = 250 // milliseconds
                colorAnimation.addUpdateListener { animator ->
                    window.statusBarColor = animator.animatedValue as Int
                }
                colorAnimation.start()
            }
        }
    }

    private fun setUpFab() {
        findViewById<FloatingActionButton>(R.id.fab_add_to_collection).setOnClickListener { view ->
            if (isFavourite) {
                fab_add_to_collection.setImageResource(R.drawable.ic_heart)
                databaseHandler.removeFromFavourites()
                isFavourite = false
                Snackbar.make(
                    view,
                    getString(R.string.fav_remove_sb_message),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(getString(R.string.undo)) {
                        databaseHandler.addToFavourites()
                        isFavourite = true
                        fab_add_to_collection.setImageResource(R.drawable.ic_heart_tick)
                        Snackbar.make(
                            view,
                            getString(R.string.fav_added_sb_message),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.show()
            } else {
                fab_add_to_collection.setImageResource(R.drawable.ic_heart_tick)
                databaseHandler.addToFavourites()
                isFavourite = true
                Snackbar.make(view, getString(R.string.fav_added_sb_message), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.undo)) {
                        databaseHandler.removeFromFavourites()
                        isFavourite = false
                        fab_add_to_collection.setImageResource(R.drawable.ic_heart)
                        Snackbar.make(
                            view,
                            getString(R.string.fav_remove_sb_message),
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkFavouriteStatus(databaseHandler)
    }

    private fun checkFavouriteStatus(databaseHandler: DatabaseHandler) {
        isFavourite = databaseHandler.getFavouriteStatus()
        if (isFavourite)
            fab_add_to_collection.setImageResource(R.drawable.ic_heart_tick)
        else
            fab_add_to_collection.setImageResource(R.drawable.ic_heart)
    }

    override fun onStateUpdate(state: UdfBaseState<AppState>, action: BaseAction): Boolean {
        return false
    }

    override fun onRawStateUpdate(state: UdfBaseState<AppState>) {
    }

    override fun onError(action: BaseAction) {
    }
}