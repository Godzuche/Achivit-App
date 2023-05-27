package com.godzuche.achivitapp.feature.feed.task_list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FilterModalBottomSheetContentBinding
import com.godzuche.achivitapp.feature.feed.util.DialogTitle
import com.godzuche.achivitapp.feature.feed.util.TaskStatus
import com.godzuche.achivitapp.feature.home.presentation.toChipText
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch
import timber.log.Timber


@OptIn(ExperimentalCoroutinesApi::class)
class FilterBottomSheetDialog : BottomSheetDialogFragment() {
    private val viewModel: TasksViewModel by activityViewModels()

    private var _binding: FilterModalBottomSheetContentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FilterModalBottomSheetContentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.bind()

    }

    private fun FilterModalBottomSheetContentBinding.bind() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.categories.collectLatest { categoryList ->
                        val chipGroup = chipGroupCategory
                        chipGroup.removeAllViews()
                        categoryList.forEachIndexed { index, title ->
                            val chip = layoutInflater.inflate(
                                R.layout.single_chip_layout,
                                chipGroup,
                                false
                            ) as Chip
                            chip.text = title
                            chip.id = index
                            chipGroup.addView(chip)
                            if (index == (categoryList.size - 1)) {
                                launch {
                                    viewModel.uiState
                                        .distinctUntilChangedBy { it.checkedCategoryFilterChipId }
                                        .collectLatest {
                                            val checkedId = it.checkedCategoryFilterChipId
                                            Timber.tag("Chip").d("Collected chip id: $checkedId")
                                            chipGroup.clearCheck()
                                            chipGroup.check(checkedId)
                                        }
                                }
                            }
                        }
                    }
                }
                launch {
                    viewModel.categoryCollections.collectLatest { collectionList ->
                        val chipGroup = chipGroupCollections
                        chipGroup.removeAllViews()
                        collectionList.forEachIndexed { index, title ->
                            val chip = layoutInflater.inflate(
                                R.layout.single_chip_layout,
                                chipGroup,
                                false
                            ) as Chip
                            chip.text = title
                            chip.id = index
                            chipGroup.addView(chip)
                            if (index == (collectionList.size - 1)) {
                                launch {
                                    viewModel.uiState
                                        .distinctUntilChangedBy { it.checkedCollectionFilterChipId }
                                        .collectLatest {
                                            val checkedId = it.checkedCollectionFilterChipId
                                            Timber.tag("Chip").d("Collected chip id: $checkedId")
                                            chipGroup.clearCheck()
                                            chipGroup.check(checkedId)
                                        }
                                }
                            }
                        }
                    }
                }

                chipGroupCategory.setOnCheckedChangeListener { group, checkedId ->
                    val categoryTitle = (group.getChildAt(checkedId) as? Chip)?.text.toString()
                    viewModel.setCheckedCategoryChip(checkedId = checkedId, categoryTitle)
                        .also {
                            Timber.tag("Category").d("checked changed id: %s", checkedId)
                        }
                    viewModel.getCategoryCollections(categoryTitle)
                }

                val statusChipGroup = binding.chipGroupStatus
                statusChipGroup.removeAllViews()
                val statusList = TaskStatus.values().map { it.name }
                statusList.forEachIndexed { index, name ->
                    val chip = layoutInflater.inflate(
                        R.layout.single_chip_layout,
                        statusChipGroup,
                        false
                    ) as Chip
                    chip.text = name.toChipText()
                    chip.id = index
                    statusChipGroup.addView(chip)
                    if (index == (statusList.size - 1)) {
                        launch {
                            viewModel.uiState
                                .distinctUntilChangedBy { it.statusFilterId }
                                .collectLatest {
                                    val checkedId = it.statusFilterId
                                    Timber.tag("Chip").d("Collected task chip id: $checkedId")
                                    binding.chipGroupStatus.clearCheck()
                                    binding.chipGroupStatus.check(checkedId)
                                }
                        }
                    }
                }
                chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

                    val checkedId = checkedIds.first()
                    val checkedTitle = (group[checkedId] as Chip).text.toString()
                    Timber.tag("Chip").d("Checked change id: $checkedId text: $checkedTitle")
                    viewModel.setCheckedStatusChip(checkedId, checkedTitle)
                }
                chipGroupCollections.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

                    val checkedId = checkedIds.first()
                    val checkedTitle = (group[checkedId] as Chip).text.toString()
//                    Timber.tag("Chip").d("Checked change id: $checkedId text: $checkedTitle")
                    viewModel.setCheckedCollectionChip(checkedId, checkedTitle)
                }
            }
        }
        icButtonAddCategory.setOnClickListener {
            findNavController().navigate(
                FilterBottomSheetDialogDirections.actionGlobalAddCategoryCollectionFragment(
                    DialogTitle.CATEGORY
                )
            )
        }
        icButtonAddCollection.setOnClickListener {
            findNavController().navigate(
                FilterBottomSheetDialogDirections.actionGlobalAddCategoryCollectionFragment(
                    DialogTitle.COLLECTION
                )
            )
        }
        icButtonDeleteCategory.setOnClickListener {
//            viewModel.deleteCategory()
        }
        icButtonDeleteCollection.setOnClickListener {
            // viewModel.deleteCollection()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /*    companion object {
            const val TAG = "Filter ModalBottomSheet"
        }*/

}