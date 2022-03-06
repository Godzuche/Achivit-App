package com.godzuche.achivitapp

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.google.android.material.chip.ChipGroup

class HomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_top_app_bar, menu)
    }

    override fun onStart() {
        super.onStart()
        activity?.findViewById<ChipGroup>(R.id.chip_group)?.visibility = View.VISIBLE
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_notifications -> {

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}