package com.example.shareway.viewstates

import com.example.shareway.models.Note
import com.example.shareway.utils.UIComponentType

sealed class NotesViewState {
    object Loading : NotesViewState()

    data class Error(
        val errorMessage: String,
        val messageType: UIComponentType
    ) : NotesViewState()

    data class NotesList(
        val notes: List<Note>
    ) : NotesViewState()
}