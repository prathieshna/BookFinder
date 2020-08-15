package lk.prathieshna.bookfinder.dialogs

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.TextView
import lk.prathieshna.bookfinder.R

class AlertDialog(private val context: Activity) {
    private lateinit var dialog: Dialog

    fun showDialog(message: String?) {
        hideDialog()
        dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_alert)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        val txtMessage: TextView = dialog.findViewById(R.id.tv_message)
        txtMessage.text = message
        dialog.show()
        val btnOk: Button = dialog.findViewById(R.id.b_ok)
        btnOk.setOnClickListener {
            hideDialog()
        }
    }

    private fun hideDialog() {
        if (::dialog.isInitialized) {
            dialog.dismiss()
        }
    }
}
