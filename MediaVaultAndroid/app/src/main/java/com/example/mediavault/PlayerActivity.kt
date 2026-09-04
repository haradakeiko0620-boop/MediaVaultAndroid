package com.example.mediavault

import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.example.mediavault.databinding.ActivityPlayerBinding
import java.io.File

class PlayerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private var player: ExoPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val paths = intent.getStringArrayListExtra("playlist") ?: arrayListOf()
        val index = intent.getIntExtra("index", 0)
        val p = ExoPlayer.Builder(this).build().also { player = it }
        binding.playerView.player = p

        p.setMediaItems(paths.map { MediaItem.fromUri(Uri.fromFile(File(it))) }, index, 0L)
        p.prepare()
        p.playWhenReady = true

        binding.repeatOneButton.setOnClickListener {
            p.repeatMode = Player.REPEAT_MODE_ONE
            p.shuffleModeEnabled = false
            updateButtons(p)
        }
        binding.repeatAllButton.setOnClickListener {
            p.repeatMode = Player.REPEAT_MODE_ALL
            updateButtons(p)
        }
        binding.shuffleButton.setOnClickListener {
            p.shuffleModeEnabled = !p.shuffleModeEnabled
            if (p.shuffleModeEnabled) p.repeatMode = Player.REPEAT_MODE_ALL
            updateButtons(p)
        }
        updateButtons(p)
    }

    private fun updateButtons(p: Player) {
        binding.repeatOneButton.text = if (p.repeatMode == Player.REPEAT_MODE_ONE) "✓ 1曲リピート" else "1曲リピート"
        binding.repeatAllButton.text = if (p.repeatMode == Player.REPEAT_MODE_ALL && !p.shuffleModeEnabled) "✓ 全曲リピート" else "全曲リピート"
        binding.shuffleButton.text = if (p.shuffleModeEnabled) "✓ シャッフル" else "シャッフル"
    }

    override fun onDestroy() {
        binding.playerView.player = null
        player?.release()
        player = null
        super.onDestroy()
    }
}
