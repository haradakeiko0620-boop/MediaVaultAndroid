package com.example.mediavault

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mediavault.databinding.ItemFileBinding
import java.io.File

class FileAdapter(
    private var items: List<File>,
    private val onClick: (File) -> Unit,
    private val onLongClick: (File) -> Unit
) : RecyclerView.Adapter<FileAdapter.VH>() {

    class VH(val binding: ItemFileBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemFileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val file = items[position]
        holder.binding.nameText.text = file.name
        holder.binding.iconText.text = when {
            file.isDirectory -> "📁"
            file.extension.lowercase() in setOf("mp3", "m4a", "aac", "wav", "flac", "ogg") -> "🎵"
            file.extension.lowercase() in setOf("mp4", "mkv", "webm", "mov", "avi") -> "🎬"
            else -> "📄"
        }
        holder.binding.root.setOnClickListener { onClick(file) }
        holder.binding.root.setOnLongClickListener { onLongClick(file); true }
    }

    override fun getItemCount() = items.size

    fun submit(newItems: List<File>) {
        items = newItems
        notifyDataSetChanged()
    }
}
