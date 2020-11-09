package com.example.shareway.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.example.shareway.databinding.NoteListItemBinding
import com.example.shareway.models.Note
import com.example.shareway.viewholders.NoteListViewHolder
import kotlinx.android.synthetic.main.article_list_item.view.*

class NoteListAdapter() :
    ListAdapter<Note, NoteListViewHolder>(DIFF_CALLBACK) {


    companion object {
        private const val TAG = "NoteListAdapter"

        val DIFF_CALLBACK: DiffUtil.ItemCallback<Note> =
            object : DiffUtil.ItemCallback<Note>() {
                override fun areItemsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem.id == newItem.id
                }

                override fun areContentsTheSame(oldItem: Note, newItem: Note): Boolean {
                    return oldItem == newItem
                }

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteListViewHolder {
        val view = NoteListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteListViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteListViewHolder, position: Int) {
        val noteItem = getItem(position)
        holder.bind(noteItem)
    }

}