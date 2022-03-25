package com.godzuche.achivitapp

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.godzuche.achivitapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)

        val isDarkMode = sharedPref.getBoolean("key_dark_mode", false)

        if (isDarkMode) AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
        else AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)

        setSupportActionBar(binding.toolbarMain)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.action_home,
                R.id.action_notifications,
                R.id.action_profile
            ),
            fallbackOnNavigateUpListener = ::onSupportNavigateUp
        )

        binding.collapsingToolbarMain.setupWithNavController(
            binding.toolbarMain, navController, appBarConfiguration
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
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

        /*binding.bottomNavView.setOnItemSelectedListener {
            when (it.itemId) {

            }
        }*/

/*        binding.bottomNavView.setOnItemReselectedListener {
            it.isEnabled = true
            // I would refresh the layout here
        }*/

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.action_home -> {
                    binding.fabAdd.show()

                    binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                        // Scrolling down
                        if (scrollY > oldScrollY + 12 && binding.fabAdd.isExtended) binding.fabAdd.shrink()
                        // Scrolling up
                        if (scrollY < oldScrollY - 12 && !binding.fabAdd.isExtended) binding.fabAdd.extend()
                        // Scrolled to top
                        if (scrollY == 0) binding.fabAdd.extend()
                    }

                }
                R.id.taskFragment -> {
                    if (!binding.fabAdd.isShown) {
                        binding.fabAdd.show()
                    }

                    binding.nestedScrollView.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->

                        if (scrollY == oldScrollY || scrollY == 0) {
                            binding.fabAdd.shrink()

                        } else {
                            binding.fabAdd.apply {
                                if (this.isExtended) {
                                    shrink()
                                }

                            }

                        }

                    }
                }
                else -> {
                    binding.fabAdd.hide()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
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

}