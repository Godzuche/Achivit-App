package com.godzuche.achivitapp.app

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.ViewGroup.MarginLayoutParams
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.godzuche.achivitapp.R
import com.godzuche.achivitapp.databinding.ActivityMainBinding
import com.godzuche.achivitapp.core.domain.repository.DarkThemeConfig
import com.godzuche.achivitapp.feature.home.presentation.HomeViewModel
import com.godzuche.achivitapp.feature.notifications.NotificationUiState
import com.godzuche.achivitapp.feature.notifications.NotificationsViewModel
import com.godzuche.achivitapp.feature.settings.DarkMode
import com.godzuche.achivitapp.feature.settings.setDarkMode
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var navController: NavController

    private val notificationsViewModel by viewModels<NotificationsViewModel>()
    private val mainActivityViewModel by viewModels<MainActivityViewModel>()
    private val homeViewModel by viewModels<HomeViewModel>()

    /*    private val currentNavigationFragment: Fragment?
            get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment)
                ?.childFragmentManager
                ?.fragments
                ?.first()*/

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { pref, prefKey ->
            when (prefKey) {
                "key_dark_mode" -> {
                    /*val defaultValue = resources.getStringArray(R.array.entry_value_theme)[0]
                    val darkMode = pref?.getString(prefKey, defaultValue)
                    setDarkMode(darkMode)*/
                }

                "key_notification_badge" -> {
                    val badgeDrawable =
                        binding.bottomNavView.getBadge(R.id.action_notifications)
                    badgeDrawable?.maxCharacterCount = 3
//                 Resets any badge number so that a numberless badge will be displayed.
                    val isShowCountBadge = pref?.getBoolean(prefKey, true)

                    lifecycleScope.launch {
                        repeatOnLifecycle(Lifecycle.State.STARTED) {
                            notificationsViewModel.notificationUiState.collectLatest { notificationUiState ->
                                when (notificationUiState) {
                                    NotificationUiState.Loading -> Unit

                                    is NotificationUiState.Success -> {
                                        val unreadNotificationsCount =
                                            notificationUiState.notifications.count { it.isRead.not() }
                                        val isUnreadNotificationsAvailable =
                                            (unreadNotificationsCount > 0)

                                        if (isUnreadNotificationsAvailable) {
                                            if (isShowCountBadge == true) {
                                                // notification count
                                                badgeDrawable?.number = unreadNotificationsCount
                                            } else badgeDrawable?.clearNumber()
                                            badgeDrawable?.isVisible = true
                                        } else {
                                            badgeDrawable?.isVisible = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    /*
        private val requestNotificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Granted
            } else {
                // Denied
                // Check if the permission has been denied twice by the user
                val isNotificationPermissionPermanentlyDeclined =
                    ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.POST_NOTIFICATIONS
                    ).not()
                val title = "Permission Required"
                val okButtonLabel = if (isNotificationPermissionPermanentlyDeclined) {
                    "Grant Permission"
                } else {
                    "OK"
                }
                val message = if (isNotificationPermissionPermanentlyDeclined) {
                    "It seems you permanently declined notifications permission." +
                            "You can go to the app settings to grant it."
                } else {
                    "This app needs access to send notification for proper functioning"
                }
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.apply {
                    this.setTitle(title)
                    setMessage(message)
                    setPositiveButton(okButtonLabel) { dialog, _ ->
                        if (isNotificationPermissionPermanentlyDeclined) {
                            goToAppSettings()
                        } else {
                            requestNotificationPermission()
                        }
                        dialog.dismiss()
                    }
                    setNegativeButton(
                        "Cancel"
                    ) { dialog, _ ->
                        if (isNotificationPermissionPermanentlyDeclined.not()) {
                            requestNotificationPermission()
                        }
                        dialog.dismiss()
                    }
                }
                alertDialogBuilder.show()
            }
        }
    */

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        var uiState by mutableStateOf<MainActivityUiState>(MainActivityUiState.Loading)

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.uiState
                    .onEach {
                        uiState = it
                    }.collect()
            }
        }

        splashScreen.setKeepOnScreenCondition {
            when (uiState) {
                MainActivityUiState.Loading -> true
                is MainActivityUiState.Success -> false
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.uiState.collect {
                    when (it) {
                        MainActivityUiState.Loading -> Unit
                        is MainActivityUiState.Success -> {

                            val darkMode = when (it.userData.darkThemeConfig) {
                                DarkThemeConfig.FOLLOW_SYSTEM -> DarkMode.followSystemDefault
                                DarkThemeConfig.DARK -> DarkMode.dark
                                DarkThemeConfig.LIGHT -> DarkMode.light
                            }

                            setDarkMode(darkMode = darkMode)
                        }
                    }
                }
            }
        }

        WindowCompat.setDecorFitsSystemWindows(window, false)
//        val windowInsetController = ViewCompat.getWindowInsetsController(window.decorView)
//        windowInsetController?.isAppearanceLightNavigationBars = true

        _binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.content_main)) { _, windowInsets ->
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
        // digit count
        badge.maxCharacterCount = 3
        badge.isVisible = false

        val isShowCountBadge = sharedPref.getBoolean("key_notification_badge", true)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                notificationsViewModel.notificationUiState.collectLatest { notificationUiState ->
                    when (notificationUiState) {
                        NotificationUiState.Loading -> Unit

                        is NotificationUiState.Success -> {
                            val unreadNotificationsCount =
                                notificationUiState.notifications.count { it.isRead.not() }
                            val isUnreadNotificationsAvailable = (unreadNotificationsCount > 0)

                            if (isUnreadNotificationsAvailable) {
                                if (isShowCountBadge) {
                                    // notification count
                                    badge.number = unreadNotificationsCount
                                } else badge.clearNumber()
                                badge.isVisible = true
                            } else {
                                badge.isVisible = false
                            }
                        }
                    }
                }
            }
        }

        binding.bottomNavView.setOnItemReselectedListener { }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val snackbar = Snackbar.make(
                    findViewById(R.id.root_main),
                    "You aren't connected to the internet",
                    Snackbar.LENGTH_INDEFINITE
                )
//            .setAnchorView(R.id.bottom_nav_view)
//            .setAnimationMode(ANIMATION_MODE_SLIDE)
                    .setAction("OK") { }
                homeViewModel.isOffline.collectLatest { isOffline ->
                    if (isOffline) {
                        snackbar.show()
                    } else {
                        snackbar.dismiss()
                    }
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.action_home -> {
                    binding.bottomNavView.visibility = View.VISIBLE
                    binding.fabAdd.hide()
                }

                R.id.action_tasks -> {
                    if (!binding.fabAdd.isShown) {
                        binding.fabAdd.apply {
                            postDelayed({ show() }, 150)
                        }
                    }
                    binding.bottomNavView.visibility = View.VISIBLE
                }

                R.id.task_detail -> {
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

                R.id.action_settings -> {
                    binding.bottomNavView.visibility = View.GONE
                    binding.fabAdd.hide()
                }

                else -> {
                    binding.fabAdd.hide()
                    binding.bottomNavView.visibility = View.GONE
                }
            }
        }

        /*if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }*/
    }

    /*    @OptIn(ExperimentalLayoutApi::class)
        override fun onStart() {
            super.onStart()

            binding.composView.apply {

                setContent {
                    val snackbarHostState = remember { SnackbarHostState() }
                    val isOffline by homeViewModel.isOffline.collectAsStateWithLifecycle()

                    LaunchedEffect(isOffline) {
                        if (isOffline) {
                            snackbarHostState.showSnackbar(
                                message = "You aren't connected to the internet",
                                duration = SnackbarDuration.Indefinite
                            )
                        }
                    }

                    Scaffold(
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        snackbarHost = { SnackbarHost(snackbarHostState) }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(it)
                                .consumeWindowInsets(it)
                        )
                    }

                }
            }

        }*/

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

    /*    private fun goToAppSettings() {
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.fromParts("package", packageName, null)
            ).also(this@MainActivity::startActivity)
        }*/

    /*    private fun requestNotificationPermission() {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    // Permission is granted
                }

                *//*ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) -> {
                // Permission was denied and additional rationale should be displayed
            }*//*

            else -> {
                // Permission has not been asked yet or it has been denied
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }*/

}