package com.godzuche.achivitapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.preference.PreferenceManager
import com.godzuche.achivitapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { p0, p1 ->
            when (p1) {
                "key_dark_mode" -> {
                    val isDarkModeEnabled = p0?.getBoolean(p1, false)
                    if (isDarkModeEnabled == true) {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                    } else {
                        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                    }
                }
                "key_notification_badge" -> {
                    val badgeDrawable =
                        binding.bottomNavView.getBadge(R.id.action_notifications)
//                 Resets any badge number so that a numberless badge will be displayed.
                    val newValue = p0?.getBoolean(p1, true)
                    if (newValue == false) {
                        badgeDrawable?.clearNumber()
                    } else {
                        badgeDrawable?.number = 100
                    }
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        binding.bottomNavView.setupWithNavController(navController)
        // Notification badge
        val badge = binding.bottomNavView.getOrCreateBadge(R.id.action_notifications)
        // Icon only
        badge.isVisible = true
        // digit count
        badge.maxCharacterCount = 3

        val isShowCountBadge = sharedPref.getBoolean("key_notification_badge", true)
        // notification count
        if (isShowCountBadge) badge.number = 100
        else badge.clearNumber()

        binding.bottomNavView.setOnItemReselectedListener { }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.action_home -> {
                    if (!binding.fabAdd.isShown) {
                        binding.fabAdd.show()
                    }
                    binding.bottomNavView.visibility = View.VISIBLE
                }
                R.id.task_fragment -> {
                    binding.bottomNavView.visibility = View.GONE
                    if (!binding.fabAdd.isShown) {
                        binding.fabAdd.show()
                    }
                }
                else -> {
                    binding.fabAdd.hide()
                    binding.bottomNavView.visibility = View.GONE
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(listener)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}