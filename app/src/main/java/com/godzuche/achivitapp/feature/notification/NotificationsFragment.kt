package com.godzuche.achivitapp.feature.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.godzuche.achivitapp.databinding.FragmentNotificationsBinding
import com.google.android.material.transition.MaterialFadeThrough

class NotificationsFragment : Fragment() {
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enterTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
        exitTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
        returnTransition = MaterialFadeThrough().apply {
            duration =
                resources.getInteger(com.google.android.material.R.integer.material_motion_duration_long_1)
                    .toLong()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onStart() {
        super.onStart()
/*        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.GONE
        activity?.findViewById<Chip>(R.id.chip_add_collection)?.visibility = View.GONE*/
        /*activity?.findViewById<BottomNavigationView>(R.id.bottom_nav_view)
            ?.visibility = View.VISIBLE*/
    }

/*    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_top_app_bar, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
//                findNavController().navigate(NotificationsFragmentDirections.actionGlobalActionNotificationsSettings())
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }*/

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}