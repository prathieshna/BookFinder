package lk.prathieshna.bookfinder.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_splash.*
import lk.prathieshna.bookfinder.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        vv_background.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.book))
        vv_background.setOnPreparedListener { mediaPlayer ->
            val videoRatio = mediaPlayer.videoWidth / mediaPlayer.videoHeight.toFloat()
            val screenRatio = vv_background.width / vv_background.height.toFloat()
            val scaleX = videoRatio / screenRatio
            if (scaleX >= 1f) {
                vv_background.scaleX = scaleX
            } else {
                vv_background.scaleY = 1f / scaleX
            }
        }
        vv_background.start()


        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }
}