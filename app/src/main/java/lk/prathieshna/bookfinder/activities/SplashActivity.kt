package lk.prathieshna.bookfinder.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import lk.prathieshna.bookfinder.R
import lk.prathieshna.bookfinder.utils.FullScreenVideoView

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var vvBackground: FullScreenVideoView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen (handles Android 12+ splash screen)
        installSplashScreen()

        // Enable edge-to-edge
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        val rlSplashRoot = findViewById<android.view.View>(R.id.rl_splash_root)
        ViewCompat.setOnApplyWindowInsetsListener(rlSplashRoot) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize views
        vvBackground = findViewById(R.id.vv_background)

        vvBackground.setVideoURI(("android.resource://" + packageName + "/" + R.raw.book).toUri())
        vvBackground.setOnPreparedListener { mediaPlayer ->
            mediaPlayer.isLooping = true

            // Resize VideoView to fill screen while maintaining aspect ratio (CENTER_CROP)
            resizeVideoView(mediaPlayer)
        }
        vvBackground.start()

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }, 5000)
    }

    private fun resizeVideoView(mediaPlayer: MediaPlayer) {
        val videoWidth = mediaPlayer.videoWidth
        val videoHeight = mediaPlayer.videoHeight

        val screenWidth = vvBackground.width
        val screenHeight = vvBackground.height

        if (videoWidth == 0 || videoHeight == 0 || screenWidth == 0 || screenHeight == 0) {
            return
        }

        val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
        val screenRatio = screenWidth.toFloat() / screenHeight.toFloat()

        val layoutParams = vvBackground.layoutParams

        if (videoRatio > screenRatio) {
            // Video is wider - match height, let width overflow
            layoutParams.width = (screenHeight * videoRatio).toInt()
            layoutParams.height = screenHeight
        } else {
            // Video is taller - match width, let height overflow
            layoutParams.width = screenWidth
            layoutParams.height = (screenWidth / videoRatio).toInt()
        }

        vvBackground.layoutParams = layoutParams

        // Center the video
        vvBackground.translationX = (screenWidth - layoutParams.width) / 2f
        vvBackground.translationY = (screenHeight - layoutParams.height) / 2f
    }
}