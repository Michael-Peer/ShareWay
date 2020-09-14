package com.example.shareway.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.R
import com.example.shareway.adapters.CategoryListAdapter
import com.example.shareway.databinding.FragmentCategoriesBinding
import com.example.shareway.listeners.ItemMoveCallbackListener
import com.example.shareway.listeners.OnCategoryClickListener
import com.example.shareway.listeners.OnStartDragListener
import com.example.shareway.listeners.UICommunicationListener
import com.example.shareway.utils.SpacingItemDecorator
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
    private lateinit var categoryListRecyclerViewAdapter: CategoryListAdapter


    private val categoriesViewModel: CategoriesViewModel by viewModel()

    lateinit var touchHelper: ItemTouchHelper


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: ")
        setHasOptionsMenu(true)

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
            gridLayoutManager = GridLayoutManager(it, 2)
            categoryListRecyclerViewAdapter = CategoryListAdapter(this, this)
            binding.categoriesRecyclerView.apply {
                addItemDecoration(SpacingItemDecorator(x))
                layoutManager = gridLayoutManager
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
                    uiCommunicationListener.onResponseReceived(
                        uiComponentType = it.messageType,
                        message = it.errorMessage
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
        val categoryDomainName = categoryListRecyclerViewAdapter.getCurrentDomainName(position)
        categoryDomainName?.let {
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }
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
        category?.let {
            categoriesViewModel.saveNewCategoryName(newCategoryName, it)
        }
    }

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
