package lk.prathieshna.bookfinder.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lk.prathieshna.bookfinder.dialogs.AlertDialog
import lk.prathieshna.bookfinder.dialogs.LoaderDialog
import lk.prathieshna.bookfinder.state.AppState
import lk.prathieshna.bookfinder.state.UdfBaseState
import lk.prathieshna.bookfinder.store.Base
import lk.prathieshna.bookfinder.store.bookFinderStore
import org.rekotlin.Store
import java.util.*

abstract class BaseActivity : AppCompatActivity(), Base<AppState> {
    override val appStore: Store<UdfBaseState<AppState>> = bookFinderStore
    private lateinit var loaderDialog: LoaderDialog
    lateinit var alertDialog: AlertDialog
    override var actionSessionIds: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderDialog = LoaderDialog(this)
        alertDialog = AlertDialog(this)
    }

    override fun hideLoader() {
        loaderDialog.hideDialog()
    }

    override fun showLoader() {
        loaderDialog.showDialog()
    }

    override fun onStart() {
        super.onStart()
        appStore.subscribe(this)
    }

    override fun onStop() {
        super.onStop()
        appStore.unsubscribe(this)
    }
}