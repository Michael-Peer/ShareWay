package com.example.shareway.ui.notes

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.shareway.R
import com.example.shareway.databinding.FragmentNoteBinding
import com.example.shareway.models.Note
import com.example.shareway.utils.modes.NoteMode
import com.example.shareway.viewmodels.NoteViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel

@InternalCoroutinesApi
@ExperimentalCoroutinesApi
class NoteFragment : Fragment() {


    private lateinit var binding: FragmentNoteBinding

    private val args: NoteFragmentArgs by navArgs()
    private val notesViewModel: NoteViewModel by viewModel()

    lateinit var noteMode: NoteMode

//    lateinit var editItem: MenuItem
//    lateinit var saveItem: MenuItem

    private var editItem: MenuItem? = null
    private var saveItem: MenuItem? = null

    private var firstTime: Boolean = true


    companion object {
        private const val TAG = "NoteFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
//        (activity as AppCompatActivity).supportRequestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        val w = (activity as AppCompatActivity).window;
//        w.setFlags(
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
//            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
//        )
        noteMode = savedInstanceState?.let {
            when (it.getInt("noteMode")) {
                0 -> NoteMode.VIEW_MODE
                1 -> NoteMode.EDIT_MODE
                2 -> NoteMode.CREATE_MODE
                else -> NoteMode.VIEW_MODE
            }
        } ?: args.noteMode

        if (savedInstanceState != null) {
            //first time
            firstTime = false
        }

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


        observeMode()
        notesViewModel.setMode(noteMode)
//        notesViewModel.setMode(args.noteMode)

//        if (noteMode == NoteMode.CREATE_MODE) {
//            binding.content.visibility = View.GONE
//            binding.contentEdit.visibility = View.VISIBLE
//        } else {
//            binding.content.text = args.note!!.content
//        }


//        createdOn.text = Instant.now().toString()

    }

    private fun observeMode() {
        activity?.invalidateOptionsMenu()
        notesViewModel.noteMode.observe(viewLifecycleOwner, Observer { mode ->
            Log.d(TAG, "observeMode: $mode ${mode.ordinal} ${mode.name}")
            noteMode = mode
            changeLayoutMode()
        })
    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.note_menu, menu)
        editItem = menu.findItem(R.id.edit)
        saveItem = menu.findItem(R.id.save)
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
        if (noteMode == NoteMode.CREATE_MODE || noteMode == NoteMode.EDIT_MODE) menu.findItem(R.id.edit).isVisible =
            false else menu.findItem(R.id.save).isVisible = false
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
                hideKeyboard()
                navToPrevious()
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
//                    notesViewModel.setText(binding.contentEdit.text.toString())
                    hideKeyboard()
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
//            NoteMode.EDIT_MODE, NoteMode.CREATE_MODE -> {
//                Log.d(TAG, "changeLayoutMode: edit text: ${binding.contentEdit.text}\n" +
//                        "viewmode = ${notesViewModel.getText()}\n" +
//                        "text = ${binding.content.text}")
//
//                binding.content.visibility = View.GONE
//                binding.contentEdit.visibility = View.VISIBLE
//                if (noteMode == NoteMode.EDIT_MODE) binding.contentEdit.setText(binding.content.text) else binding.contentEdit.setText(
//                    notesViewModel.getText()
//                )
//                editItem?.isVisible = false
//                saveItem?.isVisible = true
//                setEditTextObserver()
//            }


            /**
             *
             * Here when we're in view mode, we check if [args.note?.content] not equal to [notesViewModel.getText()].
             * If it not equal, it's mean we edited the note and we want to represent the edited note, and not the inital one.
             * I also added "ifEmpty" check, because initially they are NOT equal, because [notesViewModel.getText()] will return empty string
             *
             * **/
            NoteMode.VIEW_MODE -> {
                Log.d(
                    TAG, "changeLayoutMode \n" +
                            " edit text= ${binding.contentEdit.text}\n" +
                            "viewmodel = ${notesViewModel.getText()}\n" +
                            "text = ${binding.content.text}\n" +
                            "args.note.content = ${args.note?.content}"
                )

                binding.content.visibility = View.VISIBLE
                binding.contentEdit.visibility = View.GONE
                editItem?.isVisible = true
                saveItem?.isVisible = false
                binding.content.text =
                    if (args.note?.content != notesViewModel.getText() && notesViewModel.getText() != "") notesViewModel.getText() else args.note?.content
            }

            NoteMode.EDIT_MODE -> {
                binding.content.visibility = View.GONE
                binding.contentEdit.visibility = View.VISIBLE
                editItem?.isVisible = false
                saveItem?.isVisible = true

                /**
                 *
                 * At the first time we enter the view mode, we have pre-loaded note and we set the regular text to this pre load article content.
                 * Android by deafult save focusable/visible texts onConfigurationChange, if we enter the edit mode at the first time we still have [binding.content.text] we content.
                 * When we rotate the screen in edit mode, [binding.content.text] is not visible, hence the value of this fields won't we saved, but the value of [binding.contentEdit.text] will be save by default
                 *
                 * [setEditTextObserver] will hold the text from [binding.contentEdit.text]
                 * **/
                binding.contentEdit.setText(
                    if (binding.content.text.isNotEmpty()) {
                        notesViewModel.setText(binding.content.text.toString())
                        binding.content.text
                    } else notesViewModel.getText()
                )
                setEditTextObserver()
            }


            NoteMode.CREATE_MODE -> {
                binding.content.visibility = View.GONE
                binding.contentEdit.visibility = View.VISIBLE
                editItem?.isVisible = false
                saveItem?.isVisible = true
            }
//            NoteMode.VIEW_MODE -> {
//                Log.d(
//                    TAG, "changeLayoutMode \n" +
//                            " edit text= ${binding.contentEdit.text}\n" +
//                            "viewmodel = ${notesViewModel.getText()}\n" +
//                            "text = ${binding.content.text}\n" +
//                            "args.note.content = ${args.note?.content}"
//                )
//                binding.content.visibility = View.VISIBLE
//                binding.contentEdit.visibility = View.GONE
////
////                binding.content.setText(args.note?.content ?: notesViewModel.getText())
//                binding.content.text = if (args.note?.content == binding.content.text.toString()  ) notesViewModel.getText() else args.note?.content
//
//                editItem?.isVisible = true
//                saveItem?.isVisible = false
//            }
//            NoteMode.EDIT_MODE -> {
//                Log.d(
//                    TAG, "changeLayoutMode\n" +
//                            ": edit text: ${binding.contentEdit.text}\n" +
//                            "viewmode = ${notesViewModel.getText()}\n" +
//                            "text = ${binding.content.text}"
//                )
//
//                binding.content.visibility = View.GONE
//                binding.contentEdit.visibility = View.VISIBLE
//                binding.contentEdit.setText(if (binding.content.text.isNotEmpty()) binding.content.text.toString() else binding.contentEdit.text.toString())
//                editItem?.isVisible = false
//                saveItem?.isVisible = true
//                setEditTextObserver()
//            }
        }
    }

    private fun setEditTextObserver() {
        binding.contentEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "afterTextChanged: ")
                notesViewModel.setText(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("noteMode", noteMode.ordinal)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        //restore state
    }


}