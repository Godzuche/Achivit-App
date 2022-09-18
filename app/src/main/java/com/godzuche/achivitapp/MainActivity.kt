package com.godzuche.achivitapp

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.godzuche.achivitapp.databinding.ActivityMainBinding
import com.godzuche.achivitapp.feature_settings.setDarkMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

/*    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
            ?.childFragmentManager
            ?.fragments
            ?.first()*/

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { pref, prefKey ->
            when (prefKey) {
                "key_dark_mode" -> {
                    val defaultValue = resources.getStringArray(R.array.entry_value_theme)[0]
                    val darkMode = pref?.getString(prefKey, defaultValue)
                    setDarkMode(darkMode)
                }
                "key_notification_badge" -> {
                    val badgeDrawable =
                        binding.bottomNavView.getBadge(R.id.action_notifications)
//                 Resets any badge number so that a numberless badge will be displayed.
                    val newValue = pref?.getBoolean(prefKey, true)
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
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
//        windowInsetController?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content_main)) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updateLayoutParams<MarginLayoutParams> {
                leftMargin = insets.left
                bottomMargin = insets.bottom
                rightMargin = insets.right
                topMargin = insets.top
            }
            WindowInsetsCompat.CONSUMED
        }

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
                        binding.fabAdd.apply {
                            postDelayed({ show() }, 150)
                        }
                    }
                    binding.bottomNavView.visibility = View.VISIBLE
                }
                R.id.task_fragment -> {
                    binding.bottomNavView.visibility = View.GONE
                    if (!binding.fabAdd.isShown) {
                        binding.fabAdd.show()
                    }
                }
                R.id.action_notifications -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                    binding.fabAdd.hide()
                }
                R.id.action_profile -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                    binding.fabAdd.hide()
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