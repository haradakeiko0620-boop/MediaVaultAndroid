package com.example.mediavault

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mediavault.databinding.ActivityMainBinding
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var rootDir: File
    private lateinit var currentDir: File
    private lateinit var adapter: FileAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rootDir = StorageManager.root(this)
        currentDir = rootDir
        adapter = FileAdapter(emptyList(), ::openItem, ::showItemMenu)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        binding.backButton.setOnClickListener {
            if (currentDir != rootDir) currentDir.parentFile?.let { currentDir = it; refresh() }
        }
        binding.newFolderButton.setOnClickListener { showCreateFolderDialog() }
        binding.downloadButton.setOnClickListener { startActivity(Intent(this, DownloadActivity::class.java)) }
        refresh()
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) refresh()
    }

    private fun refresh() {
        binding.pathText.text = currentDir.relativeTo(rootDir).path.ifBlank { "MediaVault" }
        adapter.submit(StorageManager.list(currentDir))
        binding.backButton.isEnabled = currentDir != rootDir
    }

    private fun openItem(file: File) {
        if (file.isDirectory) {
            currentDir = file
            refresh()
        } else if (isMedia(file)) {
            val siblings = StorageManager.list(currentDir).filter { it.isFile && isMedia(it) }
            val index = siblings.indexOf(file).coerceAtLeast(0)
            val intent = Intent(this, PlayerActivity::class.java)
                .putStringArrayListExtra("playlist", ArrayList(siblings.map { it.absolutePath }))
                .putExtra("index", index)
            startActivity(intent)
        } else {
            Toast.makeText(this, "このファイル形式は再生対象ではありません", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isMedia(file: File): Boolean = file.extension.lowercase() in setOf(
        "mp3", "m4a", "aac", "wav", "flac", "ogg", "mp4", "mkv", "webm", "mov", "avi"
    )

    private fun showCreateFolderDialog() {
        val input = EditText(this).apply { hint = "フォルダー名" }
        AlertDialog.Builder(this)
            .setTitle("新規フォルダー")
            .setView(input)
            .setPositiveButton("作成") { _, _ ->
                if (!StorageManager.createFolder(currentDir, input.text.toString())) {
                    Toast.makeText(this, "作成できませんでした", Toast.LENGTH_SHORT).show()
                }
                refresh()
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun showItemMenu(file: File) {
        val anchor = binding.recyclerView.findViewHolderForAdapterPosition(
            StorageManager.list(currentDir).indexOf(file)
        )?.itemView ?: return
        PopupMenu(this, anchor).apply {
            menu.add("名前変更")
            menu.add("移動")
            menu.add("削除")
            setOnMenuItemClickListener {
                when (it.title.toString()) {
                    "名前変更" -> showRenameDialog(file)
                    "移動" -> showMoveDialog(file)
                    "削除" -> showDeleteDialog(file)
                }
                true
            }
            show()
        }
    }

    private fun showRenameDialog(file: File) {
        val input = EditText(this).apply { setText(file.name) }
        AlertDialog.Builder(this)
            .setTitle("名前変更")
            .setView(input)
            .setPositiveButton("変更") { _, _ ->
                if (!StorageManager.rename(file, input.text.toString()))
                    Toast.makeText(this, "名前を変更できませんでした", Toast.LENGTH_SHORT).show()
                refresh()
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun showMoveDialog(file: File) {
        val dirs = StorageManager.allDirectories(rootDir).filter { it != file && !it.toPath().startsWith(file.toPath()) }
        val labels = dirs.map { it.relativeTo(rootDir).path.ifBlank { "MediaVault" } }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("移動先を選択")
            .setItems(labels) { _, which ->
                if (!StorageManager.move(file, dirs[which]))
                    Toast.makeText(this, "移動できませんでした", Toast.LENGTH_SHORT).show()
                refresh()
            }
            .setNegativeButton("キャンセル", null)
            .show()
    }

    private fun showDeleteDialog(file: File) {
        AlertDialog.Builder(this)
            .setTitle("削除")
            .setMessage("${file.name} を削除しますか？")
            .setPositiveButton("削除") { _, _ -> StorageManager.delete(file); refresh() }
            .setNegativeButton("キャンセル", null)
            .show()
    }
}
