package com.example.mediavault

import android.content.Context
import java.io.File

object StorageManager {
    fun root(context: Context): File {
        val base = context.getExternalFilesDir(null) ?: context.filesDir
        val root = File(base, "MediaVault")
        if (!root.exists()) root.mkdirs()
        listOf("Music", "Video", "Downloads").forEach { File(root, it).mkdirs() }
        return root
    }

    fun list(dir: File): List<File> =
        dir.listFiles()?.sortedWith(compareBy<File> { !it.isDirectory }.thenBy { it.name.lowercase() }) ?: emptyList()

    fun createFolder(parent: File, name: String): Boolean {
        if (name.isBlank() || name.contains("/") || name.contains("\\")) return false
        val f = File(parent, name.trim())
        return !f.exists() && f.mkdirs()
    }

    fun rename(file: File, newName: String): Boolean {
        if (newName.isBlank() || newName.contains("/") || newName.contains("\\")) return false
        return file.renameTo(File(file.parentFile, newName.trim()))
    }

    fun move(file: File, destinationDir: File): Boolean {
        if (!destinationDir.isDirectory) return false
        val target = File(destinationDir, file.name)
        if (target.exists()) return false
        return file.renameTo(target)
    }

    fun delete(file: File): Boolean = if (file.isDirectory) file.deleteRecursively() else file.delete()

    fun allDirectories(root: File): List<File> {
        val result = mutableListOf<File>()
        fun walk(dir: File) {
            result += dir
            dir.listFiles()?.filter { it.isDirectory }?.forEach(::walk)
        }
        walk(root)
        return result
    }
}
