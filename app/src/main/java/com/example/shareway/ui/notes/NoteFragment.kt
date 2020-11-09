package com.example.shareway.ui.notes

import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shareway.R
import com.example.shareway.databinding.FragmentNoteBinding
import com.example.shareway.models.Note
import com.example.shareway.viewmodels.NoteViewModel
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class NoteFragment : Fragment() {

    private lateinit var binding: FragmentNoteBinding

    private val args: NoteFragmentArgs by navArgs()
    private val notesViewModel: NoteViewModel by viewModel()


    companion object {
        private const val TAG = "NoteFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentNoteBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        noteTitleEditText.setText(args.article.title)
//        createdOn.text = Instant.now().toString()

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                saveNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun saveNote() {
        if (binding.noteTitleEditText.text.isNotEmpty() and binding.content.text.isNotEmpty()) {
            val note = Note(
                articleUrl = args.article.url,
                title = binding.noteTitleEditText.text.toString(),
                content = binding.content.text.toString()
            )
            notesViewModel.saveNote(note)
            navToPrevious()
        }
    }

    private fun navToPrevious() {
        hideKeyboard()
        findNavController().popBackStack()
    }

    /**
     *
     * TODO: move to extensions
     *
     * **/
    private fun hideKeyboard() {
        view?.let {
            val imm = context?.getSystemService<InputMethodManager>()

            imm?.hideSoftInputFromWindow(
                it.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }


}