package com.godzuche.achivitapp.feature_task.presentation.ui_elements.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.godzuche.achivitapp.databinding.FilterModalBottomSheetContentBinding
import com.godzuche.achivitapp.feature_task.presentation.state_holder.TasksViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.ExperimentalCoroutinesApi

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
                        }
                    }
                }
                launch {
                    viewModel.collections.collectLatest { collectionList ->
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
                        }
                    }
                }
            }
        }
        icButtonAddCategory.setOnClickListener {
            findNavController().navigate(FilterBottomSheetDialogDirections.actionGlobalAddCategoryCollectionFragment(
                DialogTitle.CATEGORY
            ))
        }
        icButtonAddCollection.setOnClickListener {
            findNavController().navigate(FilterBottomSheetDialogDirections.actionGlobalAddCategoryCollectionFragment(
                DialogTitle.COLLECTION)
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val TAG = "Filter ModalBottomSheet"
    }

}