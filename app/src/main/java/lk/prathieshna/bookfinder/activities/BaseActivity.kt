package com.android.android.udf.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import lk.prathieshna.bookfinder.dialogs.LoaderDialog
import lk.prathieshna.bookfinder.store.Base
import lk.prathieshna.bookfinder.store.appStore
import java.util.*

abstract class BaseActivity : AppCompatActivity(), Base {

    override val state = appStore.state
    private lateinit var loaderDialog: LoaderDialog
    override var actionSessionIds: ArrayList<String> = ArrayList<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loaderDialog = LoaderDialog(this)
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