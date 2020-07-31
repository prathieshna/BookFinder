package lk.prathieshna.bookfinder.activities

import android.os.Bundle
import android.util.Log
import com.android.android.udf.activities.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.actions.BaseAction
import lk.prathieshna.bookfinder.actions.GetVolumesBySearch
import lk.prathieshna.bookfinder.state.AppState

class MainActivity : BaseActivity() {
    override fun onStateUpdate(state: AppState, action: BaseAction): Boolean {
        return when (action) {
            is GetVolumesBySearch -> {
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onError(action: BaseAction) {
        Log.e("error", "Activity reported error")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSearch.setOnClickListener {
            dispatchAction(
                GetVolumesBySearch.Request(
                    q = edtSearch.text.toString(),
                    startIndex = 0,
                    context = this,
                    actionId = getActionId()
                )
            )
        }
    }
}
