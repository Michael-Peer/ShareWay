package com.example.shareway.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.databinding.NoteListItemBinding
import com.example.shareway.models.Note

class NoteListViewHolder(
    private val binding: NoteListItemBinding
) :

    RecyclerView.ViewHolder(binding.root) {
    fun bind(noteItem: Note?) {
        binding.desc.text = noteItem?.title
    }
}