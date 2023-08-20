package com.godzuche.achivitapp.feature.tasks.task_list

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
import com.godzuche.achivitapp.feature.home.presentation.toModifiedStatusText
import com.godzuche.achivitapp.feature.tasks.util.DialogTitle
import com.godzuche.achivitapp.feature.tasks.util.TaskStatus
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
                                            Timber.tag("Chip")
                                                .d("Collected chip category id: $checkedId")
//                                            chipGroup.clearCheck()
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
                                        .collectLatest { uiState ->
                                            var checkedId = uiState.checkedCollectionFilterChipId
                                            if (checkedId == -1 && uiState.checkedCategoryFilterChipId == 0) {
                                                checkedId = 0
                                            }
                                            Timber.tag("Chip")
                                                .d("Collected chip collection id: $checkedId")
//                                            chipGroup.clearCheck()
                                            if (chipGroup.checkedChipId != checkedId) {
                                                chipGroup.check(checkedId)
                                            }
                                        }
                                }
                            }
                        }
                    }
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
                    chip.text = name.toModifiedStatusText()
                    chip.id = index
                    statusChipGroup.addView(chip)
                    if (index == (statusList.size - 1)) {
                        launch {
                            viewModel.uiState
                                .distinctUntilChangedBy { it.statusFilterId }
                                .collectLatest {
                                    val checkedId = it.statusFilterId
                                    Timber.tag("Chip")
                                        .d("Collected task status chip id: $checkedId")
//                                    binding.chipGroupStatus.clearCheck()
                                    binding.chipGroupStatus.check(checkedId)
                                }
                        }
                    }
                }
                chipGroupCategory.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
                    val checkedId = checkedIds.first()
                    val categoryTitle = (group[checkedId] as Chip).text.toString()
                    Timber.tag("Chip").d("Category checked changed id: %s", checkedId)
                    viewModel.setCheckedCategoryChip(checkedId = checkedId)
                    viewModel.getCategoryCollections(categoryTitle)
                }
                chipGroupCollections.setOnCheckedStateChangeListener { _, checkedIds ->
                    if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener
                    // Take the first since it is single selection mode
                    val checkedId = checkedIds.first()
                    Timber.tag("Chip").d("Collection checked changed id: %s", checkedId)
                    viewModel.setCheckedCollectionChip(checkedId)
                }
                chipGroupStatus.setOnCheckedStateChangeListener { group, checkedIds ->
                    if (checkedIds.isEmpty()) return@setOnCheckedStateChangeListener

                    val checkedId = checkedIds.first()
                    val checkedTitle = (group[checkedId] as Chip).text.toString()
                    Timber.tag("Chip").d("Status Checked change id: $checkedId text: $checkedTitle")
                    viewModel.setCheckedStatusChip(checkedId, checkedTitle)
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