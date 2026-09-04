package com.example.mediavault

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.webkit.URLUtil
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mediavault.databinding.ActivityDownloadBinding

class DownloadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDownloadBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.startDownloadButton.setOnClickListener {
            val url = binding.urlEdit.text.toString().trim()
            var fileName = binding.fileNameEdit.text.toString().trim()
            if (!URLUtil.isHttpsUrl(url) && !URLUtil.isHttpUrl(url)) {
                Toast.makeText(this, "有効なURLを入力してください", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (fileName.isBlank()) fileName = URLUtil.guessFileName(url, null, null)
            fileName = fileName.replace(Regex("[\\/:*?\"<>|]"), "_")

            try {
                val request = DownloadManager.Request(Uri.parse(url))
                    .setTitle(fileName)
                    .setDescription("MediaVaultへダウンロード中")
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, "MediaVault/Downloads/$fileName")
                val dm = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
                Toast.makeText(this, "ダウンロードを開始しました", Toast.LENGTH_LONG).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "開始できませんでした: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}
