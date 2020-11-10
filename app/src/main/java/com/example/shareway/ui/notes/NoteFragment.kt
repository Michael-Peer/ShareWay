package com.example.shareway.ui.notes

import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shareway.R
import com.example.shareway.databinding.FragmentNoteBinding
import com.example.shareway.models.Note
import com.example.shareway.viewmodels.NoteViewModel
import com.example.shareway.viewmodels.NoteViewModel.NoteMode
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class NoteFragment : Fragment() {

//    enum class NoteMode {
//        VIEW_MODE,
//        EDIT_MODE,
//        CREATE_MODE
//    }

    private lateinit var binding: FragmentNoteBinding

    private val args: NoteFragmentArgs by navArgs()
    private val notesViewModel: NoteViewModel by viewModel()
    private var noteMode = NoteMode.VIEW_MODE

    lateinit var editItem: MenuItem
    lateinit var saveItem: MenuItem


    companion object {
        private const val TAG = "NoteFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        (activity as AppCompatActivity).supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        val w =  (activity as AppCompatActivity).window;
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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


        if (args.note == null) {
            noteMode = NoteMode.CREATE_MODE
            notesViewModel.setMode(NoteMode.CREATE_MODE)
        }


        if (noteMode == NoteMode.CREATE_MODE) {
            binding.content.visibility = View.GONE
            binding.contentEdit.visibility = View.VISIBLE
        } else {
            binding.content.text = args.note!!.content
        }

        obvserveMode()


//        createdOn.text = Instant.now().toString()

    }

    private fun obvserveMode() {
        notesViewModel.noteMode.observe(viewLifecycleOwner, Observer { mode ->
            Log.d(TAG, "obvserveMode: ")
            noteMode = mode
            changeMode()
        })
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

            R.id.edit -> {
                editNote()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        editItem = menu.findItem(R.id.edit)
        saveItem = menu.findItem(R.id.save)
        if (noteMode == NoteMode.CREATE_MODE || noteMode == NoteMode.EDIT_MODE) editItem.isVisible =
            false else saveItem.isVisible = false
    }

    private fun changeMode() {
        when (noteMode) {
            NoteMode.EDIT_MODE -> {
                changeLayoutMode()
                noteMode = NoteMode.EDIT_MODE
            }
            NoteMode.VIEW_MODE -> {
                changeLayoutMode()
                noteMode = NoteMode.VIEW_MODE
            }
        }
    }

    private fun editNote() {
        notesViewModel.setMode(NoteMode.EDIT_MODE)
    }

    private fun saveNote() {
        Log.d(TAG, "saveNote: $noteMode")
        when (noteMode) {
            NoteMode.CREATE_MODE -> {
                if (binding.contentEdit.text!!.isNotEmpty()) {
                    val note = Note(
                        articleUrl = args.articleURL,
                        title = "I will replace this",
                        content = binding.contentEdit.text.toString()
                    )
                    Log.d(TAG, "saveNote: ")
                    notesViewModel.saveNote(note)
                    notesViewModel.setMode(NoteMode.VIEW_MODE)
                }
            }
            NoteMode.EDIT_MODE -> {
                Log.d(TAG, "saveNote: EDIT_MODE ${binding.contentEdit.text}")
                if (binding.contentEdit.text!!.isNotEmpty()) {
                    val note = Note(
                        id = args.note!!.id,
                        articleUrl = args.articleURL,
                        title = "I will replace this",
                        content = binding.contentEdit.text.toString()
                    )
                    notesViewModel.saveNote(note)
                    notesViewModel.setMode(NoteMode.VIEW_MODE)
                }
            }
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

    private fun changeLayoutMode() {
        when (noteMode) {
            NoteMode.EDIT_MODE, NoteMode.CREATE_MODE -> {
                binding.content.visibility = View.GONE
                binding.contentEdit.visibility = View.VISIBLE
                binding.contentEdit.setText(binding.content.text)
                editItem.isVisible = false
                saveItem.isVisible = true
            }
            NoteMode.VIEW_MODE -> {
                binding.content.visibility = View.VISIBLE
                binding.contentEdit.visibility = View.GONE
                binding.content.setText(binding.contentEdit.text)
                editItem.isVisible = true
                saveItem.isVisible = false
            }
        }
    }


}