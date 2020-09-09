package com.example.shareway.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.shareway.adapters.CategoryListAdapter
import com.example.shareway.databinding.FragmentCategoriesBinding
import com.example.shareway.listeners.ItemMoveCallbackListener
import com.example.shareway.listeners.OnCategoryClickListener
import com.example.shareway.listeners.OnStartDragListener
import com.example.shareway.listeners.UICommunicationListener
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

    }

    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart: ")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: ")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause: ")
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

        val callback : ItemTouchHelper.Callback = ItemMoveCallbackListener(adapter = categoryListRecyclerViewAdapter)

        touchHelper = ItemTouchHelper(callback)
        touchHelper.attachToRecyclerView(binding.categoriesRecyclerView)



    }

    private fun initRecyclerView() {
        activity?.let {
            gridLayoutManager = GridLayoutManager(it, 3)
            categoryListRecyclerViewAdapter = CategoryListAdapter(this, this)
            binding.categoriesRecyclerView.apply {
                layoutManager = gridLayoutManager
                adapter = categoryListRecyclerViewAdapter
            }

        }
    }

    @ExperimentalCoroutinesApi
    private fun observeViewState() {
        //        articleViewModel.articles.observe(viewLifecycleOwner, Observer {
//            Log.d(TAG, "Articles Size: ${it.size}")
//        })

//        articleViewModel.categories.observe(viewLifecycleOwner, Observer {
//            Log.d(TAG, "Categories Size: ${it.size}")
//        })

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
                    categoryListRecyclerViewAdapter.submitList(it.categories)
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

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        touchHelper.startDrag(viewHolder)

    }

//    I have weird issue with (probably) koin.
//
//    I am using a combination of `Flow`(from dao to view model) and `LiveData`(from view model to fragment)
//
//    My repository looking like this:
//
//    fun getPets(): Flow<Status> = flow {
//
//        emit(Status.LOADING)
//
//        SystemClock.sleep(5000)
//
//        emit(Status.PETS(petsDao.getPets))
//
//    }
//
//    In my fragment I'm instantiate my view model using `Koin` like this:
//
//    private val petsViewModel: PetsViewModelby viewModel()
//
//    And observe the data like this:
//
//
//
//    petsViewModel.status.observe(viewLifecycleOwner, Observer {status ->
//
//        if (status == LOADING) {
//            uiListener.displayProgressBar(true)
//        } else if(status == PETS) {
//            //Inflate view
//        }
//
//    })
//
//    The problem is when I launch the app I am getting Status.Loading but without any view, without app bar etc etc. like the app is freezing.
//
//    and when the status changes to Staus.PETS I am getting the pets data and the view
//
//    It's like the view model is too fast and it's calling to repository before I even have a chance to inflate the view(I logged the lifecycle and it does call before onResume)
//
//    How can I fix my code?


}