package com.godzuche.achivitapp.feature_tasks_feed.search_tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature_tasks_feed.task_list.TasksViewModel
import com.google.android.material.R.integer
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SearchFragment : Fragment() {
    /*private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!*/

    private val viewModel: TasksViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        /*_binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root*/

        return ComposeView(requireContext()).apply {
            id = R.id.search_tasks_fragment
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isTransitionGroup = true
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    SearchTasks()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
/*        super.onViewCreated(view, savedInstanceState)
        binding.searchToolbar.setNavigationOnClickListener { findNavController().navigateUp() }*/

        /*       val searchView = binding.searchView
               searchView.apply {
       //            setIconifiedByDefault(false)
       //            queryHint = getString(R.string.search_task)
                   onQueryTextChange(binding,
                       { queryText -> viewModel.accept(TasksUiEvent.Search(queryText)) },
                       { queryText -> viewModel.accept(TasksUiEvent.OnSearch(queryText)) }
                   )
       //             setOnCloseListener {
       //                 viewModel.onSearchClosed()
       //             }
               }*/
    }

    @OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
    @Composable
    fun SearchTasks() {
        Scaffold {
            SearchScreen(modifier = Modifier
                .padding(it)
                .consumeWindowInsets(it))
        }
    }

    @Composable
    fun SearchScreen(modifier: Modifier = Modifier) {
        Box(modifier = Modifier
            .fillMaxSize()
            .then(modifier))
    }

/*    override fun onStart() {
        super.onStart()
*//*        val appBarLayout = activity?.findViewById<AppBarLayout>(R.id.app_bar_layout)
        appBarLayout?.visibility = View.GONE*//*
    }

    fun clearToolbarMenu() {
        binding.searchToolbar.menu.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }*/

}