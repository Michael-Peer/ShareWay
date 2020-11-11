package com.example.shareway.viewmodels

import androidx.lifecycle.*
import com.example.shareway.models.Note
import com.example.shareway.repositories.ArticleRepository
import com.example.shareway.utils.UIComponentType
import com.example.shareway.utils.modes.NoteMode
import com.example.shareway.viewstates.NotesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class NoteViewModel constructor(
    private val articleRepository: ArticleRepository
) : ViewModel() {


    private var _noteMode = MutableLiveData<NoteMode>()
    val noteMode: LiveData<NoteMode>
        get() = _noteMode


    private var lastSource: LiveData<NotesViewState.NotesList>? = null
    private val _states = MediatorLiveData<NotesViewState>()
    val states: LiveData<NotesViewState> = _states

    private var text: String = ""


    fun setMode(noteMode: NoteMode) {
        _noteMode.value = noteMode
    }

    fun setText(text: String) {
        this.text = text
    }

    fun getText(): String = text


    fun saveNote(note: Note) {
        viewModelScope.launch {
            articleRepository.insertNote(note)
        }
    }

    fun getNotes(articleURL: String) {
        _states.value = NotesViewState.Loading

        lastSource?.let {
            _states.removeSource(it)
        }

        try {
            val notes = articleRepository.getNotes(articleURL).map {
                NotesViewState.NotesList(it)
            }.asLiveData()
            _states.addSource(notes) {
                _states.value = it
            }

            lastSource = notes
        } catch (e: Exception) {
            _states.value = NotesViewState.Error(
                errorMessage = "Something went wrong while getting notes",
                messageType = UIComponentType.Toast
            )
        }


    }


}