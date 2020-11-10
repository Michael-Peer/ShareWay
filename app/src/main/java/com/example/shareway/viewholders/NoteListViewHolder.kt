package com.example.shareway.viewholders

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.databinding.NoteListItemBinding
import com.example.shareway.listeners.OnNoteClickListener
import com.example.shareway.models.Note

class NoteListViewHolder(
    private val binding: NoteListItemBinding,
    private val onNoteClickListener: OnNoteClickListener
) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
    fun bind(noteItem: Note?) {
        binding.notePreviewText.text = noteItem?.content
    }

    init {
        binding.root.setOnClickListener(this)
    }


    override fun onClick(v: View?) {
        onNoteClickListener.onNoteClick(adapterPosition)
    }
}