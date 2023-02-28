package com.godzuche.achivitapp.feature_tasks_feed.search_tasks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.feature_tasks_feed.task_list.TasksViewModel
import com.google.android.material.R.integer
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class SearchFragment : Fragment() {

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
                    SearchTasks(viewModel)
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
    fun SearchTasks(viewModel: TasksViewModel) {
        Scaffold(
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) {
            SearchScreen(
                modifier = Modifier
                    .padding(it)
                    .consumeWindowInsets(it),
                viewModel
            )
        }
    }

    @Composable
    fun rememberLifecycleEvent(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current): Lifecycle.Event {
        var state by remember {
            mutableStateOf(Lifecycle.Event.ON_ANY)
        }
        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                state = event
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
        return state
    }

    @OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
    @Composable
    fun SearchScreen(
        modifier: Modifier = Modifier,
        viewModel: TasksViewModel,
    ) {
        val latestLifecycleEvent = rememberLifecycleEvent()

        var query by remember {
            mutableStateOf("")
        }
        var active by remember {
            mutableStateOf(false)
        }

        /*if (latestLifecycleEvent == Lifecycle.Event.ON_RESUME) {
            active = true
        }*/

        val tasks = viewModel.tasksPagingDataFlow.collectAsLazyPagingItems()
        val keyboardController = LocalSoftwareKeyboardController.current

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SearchBar(
                query = query,
                onQueryChange = {
                    query = it
                    active = it.isNotEmpty()
                },
                onSearch = {
                    active = it.isNotEmpty()
                    keyboardController?.hide()
                },
                active = active,
                onActiveChange = {
                },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    Icon(imageVector = Icons.Filled.Clear,
                        contentDescription = "Clear",
                        modifier = Modifier.clickable {
                            query = ""
                            active = query.isNotEmpty()

                        }
                    )
                },
                windowInsets = WindowInsets(0, 0, 0, 0),
                placeholder = {Text("Search tasks")},
                modifier = Modifier.fillMaxWidth().navigationBarsPadding()
            ) {
                LazyColumn {
                    items(items = tasks) {
                        Text(it?.title ?: "", color = Color.Black)
                    }
                }
            }
        }
    }
}