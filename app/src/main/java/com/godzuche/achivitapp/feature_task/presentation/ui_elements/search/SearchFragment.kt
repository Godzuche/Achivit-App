package com.godzuche.achivitapp.feature_task.presentation.ui_elements.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.databinding.FragmentSearchBinding
import com.godzuche.achivitapp.feature_task.presentation.TasksUiEvent
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.godzuche.achivitapp.feature_task.presentation.util.onQueryTextChange
import com.google.android.material.R.integer
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TasksViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(false)

        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1).toLong()
        }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration =
                resources.getInteger(integer.material_motion_duration_long_1).toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }

        val searchView = binding.searchView
        searchView.apply {
//            setIconifiedByDefault(false)
//            queryHint = getString(R.string.search_task)
            onQueryTextChange(binding,
                { queryText -> viewModel.accept(TasksUiEvent.Search(queryText)) },
                { queryText -> viewModel.accept(TasksUiEvent.OnSearch(queryText)) }
            )
            /* setOnCloseListener {
                 viewModel.onSearchClosed()
             }*/
        }
    }

    override fun onStart() {
        super.onStart()
/*        val appBarLayout = activity?.findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout?.visibility = View.GONE*/
    }

    fun clearToolbarMenu() {
        binding.searchToolbar.menu.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}