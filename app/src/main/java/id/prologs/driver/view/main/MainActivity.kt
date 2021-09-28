package id.prologs.driver.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.prologs.driver.BuildConfig
import id.prologs.driver.R
import id.prologs.driver.databinding.ActivityMainBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.Check
import id.prologs.driver.model.Logout
import id.prologs.driver.model.Track
import id.prologs.driver.model.Update
import id.prologs.driver.util.AppPreference
import id.prologs.driver.util.ForegroundOnlyLocationService
import id.prologs.driver.util.SharedPreferenceUtil
import id.prologs.driver.view.assigned.AssignedFragment
import id.prologs.driver.view.base.BaseActivity
import id.prologs.driver.view.history.HistoryFragment
import id.prologs.driver.view.manual.ManualActivity
import id.prologs.driver.view.profile.ProfileFragment
import id.prologs.driver.view.scanner.ScannerActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.inject
import java.io.IOException
import java.util.*

class MainActivity : BaseActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var binding: ActivityMainBinding
    private val viewModel by inject<MainViewModel>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private lateinit var locationManager: LocationManager
    var gpsStatus = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@MainActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@MainActivity)
            })
            snackbarMessage.observe(this@MainActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@MainActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@MainActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })
            loginSuccess.observe(this@MainActivity, Observer {
                action.isChecked = true
            })
            logoutSuccess.observe(this@MainActivity, Observer {
                action.isChecked = false
            })
            data.observe(this@MainActivity, Observer {
                // TODO: Step 1.0, Review Permissions: Checks and requests if needed.
                if (foregroundPermissionApproved()) {
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                        ?: Log.d(TAG, "Service Not Bound")
                } else {
                    requestForegroundPermissions()
                }
            })


        }
        viewModel.appSetting(Update(
            driver_id = AppPreference.getProfile().idDriver
        ))
        setView()
        checkGpsStatus()
    }

    private fun checkGpsStatus() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        gpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (gpsStatus) {

        } else {
            Snackbar.make(view_parent, "GPS is disabled!", Snackbar.LENGTH_LONG)
                .setAction("Enable") {
                    startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }.show()
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.page_1 -> {
                val fragment = AssignedFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_2 -> {
                val fragment = HistoryFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
            R.id.page_3 -> {
                val fragment = ProfileFragment.newInstance()
                addFragment(fragment)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }
    
    private fun checkPermission(){
        Dexter.withActivity(this)
            .withPermission(Manifest.permission.CAMERA)
            .withListener(object : PermissionListener {
                override fun onPermissionGranted(response: PermissionGrantedResponse) {
                    dialogPickup()
                }

                override fun onPermissionDenied(response: PermissionDeniedResponse) {}
                override fun onPermissionRationaleShouldBeShown(
                    permission: PermissionRequest,
                    token: PermissionToken
                ) {
                }
            }).check()
    }
    private fun setView(){

        action.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                viewModel.login(Check(AppPreference.getProfile().idDriver.toInt()))
            }else {
                viewModel.logout(Logout(AppPreference.getAttendance().toInt()))
            }
        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
            )
        }


        bottom_navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        val fragment = AssignedFragment.newInstance()
        addFragment(fragment)

        ic_scan.setOnClickListener {
            checkPermission()
        }
        ic_notif.setOnClickListener {
            startActivity(Intent(this, NotificationActivity::class.java))
        }
        btn_add.setOnClickListener {
            startActivity(Intent(this@MainActivity, ManualActivity::class.java))
        }

        foregroundOnlyBroadcastReceiver = ForegroundOnlyBroadcastReceiver()

        sharedPreferences =
            getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE)


    }
    private fun dialogPickup() {
        val dialogPickup = AlertDialog.Builder(this)
        dialogPickup.setTitle("Pickup")
        dialogPickup.setMessage("Select one pickup method")
        dialogPickup.setPositiveButton(
            "Scan Barcode"
        ) { dialog, which -> startActivity(Intent(this@MainActivity, ScannerActivity::class.java)) }
        //        dialogPickup.setNeutralButton("Input AWB", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//            }
//        });
        dialogPickup.show()
    }


    private var foregroundOnlyLocationServiceBound = false

    // Provides location updates for while-in-use feature.
    private var foregroundOnlyLocationService: ForegroundOnlyLocationService? = null

    // Listens for location broadcasts from ForegroundOnlyLocationService.
    private lateinit var foregroundOnlyBroadcastReceiver: ForegroundOnlyBroadcastReceiver

    private lateinit var sharedPreferences: SharedPreferences

    // Monitors connection to the while-in-use service.
    private val foregroundOnlyServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as ForegroundOnlyLocationService.LocalBinder
            foregroundOnlyLocationService = binder.service
            foregroundOnlyLocationServiceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            foregroundOnlyLocationService = null
            foregroundOnlyLocationServiceBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        foregroundOnlyLocationService?.subscribeToLocationUpdates()

        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        val serviceIntent = Intent(this, ForegroundOnlyLocationService::class.java)
        bindService(serviceIntent, foregroundOnlyServiceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onResume() {
        super.onResume()
        checkGpsStatus()
        foregroundOnlyLocationService?.subscribeToLocationUpdates()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            foregroundOnlyBroadcastReceiver,
            IntentFilter(
                ForegroundOnlyLocationService.ACTION_FOREGROUND_ONLY_LOCATION_BROADCAST)
        )
    }

    override fun onPause() {
//        LocalBroadcastManager.getInstance(this).unregisterReceiver(
//            foregroundOnlyBroadcastReceiver
//        )
        super.onPause()
    }

    override fun onStop() {
        if (foregroundOnlyLocationServiceBound) {
            unbindService(foregroundOnlyServiceConnection)
            foregroundOnlyLocationServiceBound = false
        }
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)


        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        foregroundOnlyLocationService?.unsubscribeToLocationUpdates()

    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        // Updates button states if new while in use location is added to SharedPreferences.
        if (key == SharedPreferenceUtil.KEY_FOREGROUND_ENABLED) {

        }
    }

    // TODO: Step 1.0, Review Permissions: Method checks if permissions approved.
    private fun foregroundPermissionApproved(): Boolean {
        return PackageManager.PERMISSION_GRANTED == ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    // TODO: Step 1.0, Review Permissions: Method requests permissions.
    private fun requestForegroundPermissions() {
        val provideRationale = foregroundPermissionApproved()

        // If the user denied a previous request, but didn't check "Don't ask again", provide
        // additional rationale.
        if (provideRationale) {
            Snackbar.make(
                view_parent,
                R.string.permission_rationale,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.ok) {
                    // Request permission
                    ActivityCompat.requestPermissions(
                        this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
                    )
                }
                .show()
        } else {
            Log.d(TAG, "Request foreground only permission")
            ActivityCompat.requestPermissions(
                this@MainActivity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE
            )
        }
    }

    // TODO: Step 1.0, Review Permissions: Handles permission result.
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionResult")

        when (requestCode) {
            REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE -> when {
                grantResults.isEmpty() ->
                    // If user interaction was interrupted, the permission request
                    // is cancelled and you receive empty arrays.
                    Log.d(TAG, "User interaction was cancelled.")
                grantResults[0] == PackageManager.PERMISSION_GRANTED ->
                    // Permission was granted.
                    foregroundOnlyLocationService?.subscribeToLocationUpdates()
                else -> {
                    // Permission denied.

                    Snackbar.make(
                        view_parent,
                        R.string.permission_denied_explanation,
                        Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.settings) {
                            // Build intent that displays the App settings screen.
                            val intent = Intent()
                            intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                            val uri = Uri.fromParts(
                                "package",
                                BuildConfig.APPLICATION_ID,
                                null
                            )
                            intent.data = uri
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)
                        }
                        .show()
                }
            }
        }
    }


    /**
     * Receiver for location broadcasts from [ForegroundOnlyLocationService].
     */
    private inner class ForegroundOnlyBroadcastReceiver : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(
                ForegroundOnlyLocationService.EXTRA_LOCATION
            )

            if (location != null) {
                viewModel.trackDriver(
                    Track(
                        AppPreference.getProfile().idDriver,
                        location.latitude.toString(),
                        location.longitude.toString(),
                        "",
                        "",
                        ""

                    )
                )
            }
        }
    }

    companion object {
        private const val PERMISSION_ID = 42

        private const val TAG = "MainActivity"
        private const val REQUEST_FOREGROUND_ONLY_PERMISSIONS_REQUEST_CODE = 34
    }
}