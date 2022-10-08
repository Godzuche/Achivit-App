package com.godzuche.achivitapp.feature_task.presentation.tasks

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.FilterModalBottomSheetContentBinding
import com.godzuche.achivitapp.feature_task.presentation.util.DialogTitle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


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
                                    viewModel.checkedChipId.collectLatest {
                                        Log.d("Chip", "Collected chip id: $it")
                                        chipGroup.clearCheck()
                                        chipGroup.check(it)
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
                        }
                    }
                }
                launch {
                    chipGroupCategory.setOnCheckedChangeListener { group, checkedId ->
                        viewModel.setCheckedCategoryChip(checkedId = checkedId).also {
                            Log.d("Category", "checked changed id: $checkedId")
                        }
                        viewModel.getCategoryCollections((group.getChildAt(checkedId) as? Chip)?.text.toString())
                        Log.d(
                            "Category",
                            "checked category text : ${(group.getChildAt(checkedId) as? Chip)?.text.toString()}"
                        )
                    }
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