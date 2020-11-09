package com.example.shareway.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.R
import com.example.shareway.adapters.CategoryListAdapter
import com.example.shareway.databinding.FragmentCategoriesBinding
import com.example.shareway.listeners.*
import com.example.shareway.utils.SpacingItemDecorator
import com.example.shareway.utils.UIComponentType
import com.example.shareway.viewmodels.CategoriesViewModel
import com.example.shareway.viewstates.CategoriesViewState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.koin.androidx.viewmodel.ext.android.viewModel


@ExperimentalCoroutinesApi
class CategoriesFragment : Fragment(), OnCategoryClickListener, OnStartDragListener {


    companion object {
        private const val TAG = "CategoriesFragment"
    }

    private lateinit var uiCommunicationListener: UICommunicationListener
    private lateinit var binding: FragmentCategoriesBinding
    private lateinit var gridLayoutManager: GridLayoutManager
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var categoryListRecyclerViewAdapter: CategoryListAdapter


    private val categoriesViewModel: CategoriesViewModel by viewModel()

    lateinit var touchHelper: ItemTouchHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setHasOptionsMenu(true)
        (activity as AppCompatActivity?)?.supportActionBar?.show()


    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    /**
     *
     * Because recycler view does not save automatically the order of items, every time the user lives the app I need to save the order of the list.
     * I created another dao with REPLACE strategy, thar save again the items to the database but with the right order
     *
     * **/
    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
        val imm =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.root.windowToken, 0)
        categoriesViewModel.saveItemsPosition(categoryListRecyclerViewAdapter.currentList)
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop: ")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: ")
        // Inflate the layout for this fragment
//        return inflater.inflate(R.layout.fragment_categories, container, false)
        return FragmentCategoriesBinding.inflate(inflater, container, false).apply {
            binding = this
        }.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated: ")


        /**
         *
         * For testing
         *
         * **/
//        val sharedPref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
//        val editor = sharedPref.edit()
//        editor.putBoolean("finishedOnBoarding", false)
//        editor.apply()

        observeViewState()
        initRecyclerView()

        //TODO: Move to function
        val callback: ItemTouchHelper.Callback =
            ItemMoveCallbackListener(adapter = categoryListRecyclerViewAdapter)

        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.categoriesRecyclerView)


    }

    /**
     *
     * Set recycler view & recycler view params
     *
     * **/
    private fun initRecyclerView() {
        val x = (resources.displayMetrics.density * 8).toInt() //converting dp to pixels
        activity?.let {
//            gridLayoutManager = GridLayoutManager(it, 2)
            linearLayoutManager = LinearLayoutManager(it)
            categoryListRecyclerViewAdapter = CategoryListAdapter(this, this)
            binding.categoriesRecyclerView.apply {
                addItemDecoration(SpacingItemDecorator(x))
//                layoutManager = gridLayoutManager
                layoutManager = linearLayoutManager
                adapter = categoryListRecyclerViewAdapter
            }

        }
    }

    @ExperimentalCoroutinesApi
    private fun observeViewState() {

        categoriesViewModel.viewState.observe(viewLifecycleOwner, Observer {

            uiCommunicationListener.displayProgressBar(it is CategoriesViewState.Loading)
            when (it) {

//                is CategoriesViewState.Loading -> {
//                    Log.d(TAG, "LOADING")
//                    uiCommunicationListener.displayProgressBar(true)
//                }

                is CategoriesViewState.Error -> {
//                    uiCommunicationListener.displayProgressBar(false)
//                    uiCommunicationListener.onResponseReceived(
//                        uiComponentType = it.messageType,
//                        message = it.errorMessage
//                    )

                    uiCommunicationListener.onResponseReceived(
                        response = Response(
                            //TODO: probably need to change to different dialog functions
                            uiComponentType = it.messageType,
                            message = it.errorMessage
                        )

                    )
                    Log.d(TAG, "ERROR ${it.errorMessage}")

                }

                is CategoriesViewState.CategoryList -> {
//                    uiCommunicationListener.displayProgressBar(false)
                    Log.d(TAG, "CategoriesViewState.CategoryList: ${it.categories.size}")

//                    categoryListRecyclerViewAdapter.submitList(it.categories)
                    categoryListRecyclerViewAdapter.modifyList(it.categories)
                    Log.d(TAG, "DATA")

                }
            }
        })
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationListener
        } catch (e: ClassCastException) {
            Log.d(TAG, "$context must implement UICommunicationListener")
        }
    }

    override fun onCategoryClick(position: Int) {
        Log.d(TAG, "onCategoryClick: position: $position")
        val categoryDomainName = categoryListRecyclerViewAdapter.getCurrentCategory(position)
        categoryDomainName?.let {
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToArticleFragment(it.originalCategoryName, it.filterMode)
            findNavController().navigate(action)
        }
//        Log.d(TAG, "onCategoryClick: position: $position")
//        val categoryDomainName = categoryListRecyclerViewAdapter.getCurrentDomainName(position)
//        categoryDomainName?.let {
//            val action = CategoriesFragmentDirections.actionCategoriesFragmentToArticleFragment(it)
//            findNavController().navigate(action)
//        }
    }

    override fun onTextClick(position: Int) {
//        binding.tex
    }

    /**
     *
     * In edit mode we've check icon to indicate when user done editing.
     * TODO: check icon on keyboard
     *
     * **/
    override fun onCheckIconClick(newCategoryName: String, position: Int) {

        val category = categoryListRecyclerViewAdapter.getCurrentCategory(position)
        Log.d(TAG, "onCheckIconClick: $newCategoryName ${category?.originalCategoryName}")
        category?.let {
            categoriesViewModel.saveNewCategoryName(newCategoryName, it)
        }
    }

    /**
     *
     * This function called when the reset icon clicked when inside edit mode, the function will reset the name to the original name
     * we need to call [saveItemsPosition] because we're updating the screen, and when the screen gets updated the order of the items changes too.
     * Because I'm calling to save position only in [onPause] (in order to save DB call), I need to save the position before I'm updating the DB
     *
     * Inside the Repo I've now 2 [saveItemsPosition], one that launch coroutine, and second that doesn't.
     * Because 2 coroutines runs in parallel, The text update (usually) ends before the list update, and it messes the order.
     * Inside coroutine thae code run synchronously so I created  saveItemsPosition function that doesn't launch any coroutine, and called [saveItemsPosition] before I'm updating the original name
     *
     *
     * **UPDATE**:
     * It still have little "glitches" in some edge cases(Doesn't mess the order, just go back to old place for a fraction of second and go back to the correct place)
     * I believe that's because it's done update the DB, and until the updates go back to the recycler view, the text update is done. As I said, the frequency is RARE, but still happen.
     * I decided to took another approach, I'm updating the screen with the original name from the in app model, and inserting the name to the DB, like I'm doing with the accept edit button.
     *
     *
     *
     * **/
    override fun onResetIconClick(adapterPosition: Int) {
//        categoriesViewModel.saveItemsPosition(categoryListRecyclerViewAdapter.currentList)
        val category = categoryListRecyclerViewAdapter.getCurrentCategory(adapterPosition)
        category?.let {
            categoriesViewModel.resetToOriginalName(it, categoryListRecyclerViewAdapter.currentList)
        }
    }


    /**
     *
     * [onEnterEditMode] + [onExitEditMode]
     *
     * In those callbacks(through onCategoryListener) I'm disabling clicks on item when the user enters the edit mode, other than the item that currently being edited.
     * TODO: Consider disable also other click types(binding.root, long etc) even on the edited item.
     * I've [isClickable] field in the category model, which is ignored by room.
     * when enter I simply set this to false to all items other than the current item, and opposite when exiting
     *
     * TODO: Handle home button press(which is currently returning to edit mode, but probably good UX will be if the user leaves the app without confirm editing is to reset editing and exit edit mode. Also, notify user why he can't click on other items while in editing mode
     *
     * **/

    override fun onEnterEditMode(adapterPosition: Int) {
        Log.d(TAG, "onEnterEditMode: ")
        val list = categoryListRecyclerViewAdapter.currentList
        list.forEachIndexed { index, category ->
            if (index != adapterPosition) {
                category.isClickable = false
                categoryListRecyclerViewAdapter.changeEditMode(true)
                categoryListRecyclerViewAdapter.notifyItemChanged(index)
            }
            Log.d(
                TAG,
                "onEnterEditMode: category ${category.originalCategoryName} isClickable ${category.isClickable} position $index"
            )
        }
//        categoryListRecyclerViewAdapter.notifyDataSetChanged()
    }

    override fun onExitEditMode(adapterPosition: Int) {
        Log.d(TAG, "onExitEnterMode: ")
        val list = categoryListRecyclerViewAdapter.currentList
        list.forEachIndexed { index, category ->
            if (index != adapterPosition) {

                category.isClickable = true
                categoryListRecyclerViewAdapter.changeEditMode(false)
                categoryListRecyclerViewAdapter.notifyItemChanged(index)

            }
            Log.d(
                TAG,
                "onExitEnterMode: category ${category.originalCategoryName} isClickable ${category.isClickable} position $index"
            )
        }
    }

    override fun onMarkAsRead(adapterPosition: Int) {
        val category = categoryListRecyclerViewAdapter.getCurrentCategory(adapterPosition)
        category?.let {
            categoriesViewModel.onMarkAsRead(it)

        } ?: uiCommunicationListener.onResponseReceived(
            Response(
                message = getString(R.string.mark_as_read_category_error),
                uiComponentType = UIComponentType.Toast
            )
        )
    }

    override fun onDelete(adapterPosition: Int) {
        val category = categoryListRecyclerViewAdapter.getCurrentCategory(adapterPosition)
        category?.let {
            categoriesViewModel.deleteCategory(it)

        } ?: uiCommunicationListener.onResponseReceived(
            Response(
                message = getString(R.string.delete_Category_error),
                uiComponentType = UIComponentType.Toast
            )
        )    }


    /**
     *
     * Activate when we start the dragging process
     *
     * **/
    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        Log.d(TAG, "onStartDrag: ")
        touchHelper.startDrag(viewHolder)

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        Log.d(TAG, "onCreateOptionsMenu: ")
        inflater.inflate(R.menu.category_menu, menu)

        val menuItem = menu.findItem(R.id.filter_menu_search)
        val searchView = menuItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                Log.i(TAG, "onQueryTextChange: " + newText);

                /**
                 *
                 * Send the search input to the filter function inside the adapter
                 *
                 * **/
                categoryListRecyclerViewAdapter.filter(newText);
                return false; }
        })
    }


    /**
     *
     * Select which order to show
     * name -> Order categories by name
     * date -> Order categories by date
     * **/
    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.name -> {
                item.isChecked = true
                categoriesViewModel.getCategoriesByName()
                return true
            }
            R.id.date -> {
                item.isChecked = true
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }


}
