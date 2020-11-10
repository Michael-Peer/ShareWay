package com.example.shareway.ui.notes

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.example.shareway.adapters.NoteListAdapter
import com.example.shareway.databinding.FragmentNoteListBinding
import com.example.shareway.listeners.OnNoteClickListener
import com.example.shareway.listeners.Response
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.ui.CategoriesFragment
import com.example.shareway.ui.CategoriesFragmentDirections
import com.example.shareway.viewmodels.NoteViewModel
import com.example.shareway.viewstates.NotesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@ExperimentalCoroutinesApi
@InternalCoroutinesApi
class NoteListFragment : Fragment(), OnNoteClickListener {

    companion object {
        private const val TAG = "NoteListFragment"
    }

    private lateinit var uiCommunicationListener: UICommunicationListener

    private lateinit var binding: FragmentNoteListBinding

    private lateinit var noteListRecyclerViewAdapter: NoteListAdapter
    private lateinit var gridLayoutManager: GridLayoutManager


    private val args: NoteListFragmentArgs by navArgs()
    private val notesViewModel: NoteViewModel by viewModel()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentNoteListBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        getNotes()
        observeNotes()

    }

    private fun observeNotes() {
        notesViewModel.states.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            uiCommunicationListener.displayProgressBar(it is NotesViewState.Loading)

            when (it) {
                is NotesViewState.Error -> {
                    uiCommunicationListener.onResponseReceived(
                        response = Response(
                            uiComponentType = it.messageType,
                            message = it.errorMessage
                        )
                    )
                    Log.d(TAG, "ERROR ${it.errorMessage}")

                }
                is NotesViewState.NotesList -> {
                    noteListRecyclerViewAdapter.submitList(it.notes)
                }
            }

        })


    }

    private fun getNotes() {
        notesViewModel.getNotes(args.articleURL)
    }

    private fun initRecyclerView() {
        activity?.let {
            gridLayoutManager = GridLayoutManager(it, 2)
            noteListRecyclerViewAdapter = NoteListAdapter(this)
            binding.notes.apply {
                layoutManager = gridLayoutManager
                adapter = noteListRecyclerViewAdapter
            }

        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement UICommunicationListener")
        }
    }

    override fun onNoteClick(position: Int) {
         val note = noteListRecyclerViewAdapter.getNote(position)
        note?.let {
                val action = NoteListFragmentDirections.actionNoteListFragmentToNoteFragment(args.articleURL, it)
                findNavController().navigate(action)
            }

           }


}