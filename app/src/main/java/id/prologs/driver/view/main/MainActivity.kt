package id.prologs.driver.view.main

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import id.prologs.driver.R
import id.prologs.driver.databinding.ActivityMainBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.Check
import id.prologs.driver.model.Logout
import id.prologs.driver.model.Track
import id.prologs.driver.model.Update
import id.prologs.driver.util.AppPreference
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

class MainActivity : BaseActivity() {

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
                getLastLocation()
                requestNewLocationData()
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

    override fun onResume() {
        super.onResume()
        checkGpsStatus()
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

//        binding.subscribeButton.setOnClickListener {
//            Log.d(TAG, "Subscribing to weather topic")
//            // [START subscribe_topics]
//            Firebase.messaging.subscribeToTopic("weather")
//                .addOnCompleteListener { task ->
//                    var msg = getString(R.string.msg_subscribed)
//                    if (!task.isSuccessful) {
//                        msg = getString(R.string.msg_subscribe_failed)
//                    }
//                    Log.d(TAG, msg)
//                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//                }
//            // [END subscribe_topics]
//        }
//
//        binding.logTokenButton.setOnClickListener {
//            // Get token
//            // [START log_reg_token]


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
    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {

                mFusedLocationClient?.lastLocation?.addOnCompleteListener(this) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        val addresses = getLocationDetail(location.latitude, location.longitude)
                        addresses?.let { it1 ->
                            if (addresses.isNotEmpty()) {
                                viewModel.address.value = addresses[0].getAddressLine(0)
                                viewModel.city.value = addresses[0].locality
                                viewModel.lat.value = location.latitude.toString()
                                viewModel.lon.value = location.longitude.toString()

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
                }
            } else {
                Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }
    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = viewModel.data.value!!.trackInterval.toLong() * 1000
        mLocationRequest.fastestInterval = viewModel.data.value!!.trackInterval.toLong() * 1000
        mLocationRequest.numUpdates = Int.MAX_VALUE

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient!!.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.myLooper()
        )
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var mLastLocation: Location = locationResult.lastLocation
            val addresses = getLocationDetail(mLastLocation.latitude, mLastLocation.longitude)
            addresses?.let { it1 ->
                if (addresses.isNotEmpty()) {
                    viewModel.address.value = addresses[0].getAddressLine(0)
                    viewModel.city.value = addresses[0].locality
                    viewModel.lat.value = mLastLocation.latitude.toString()
                    viewModel.lon.value = mLastLocation.longitude.toString()

                    viewModel.trackDriver(
                        Track(
                            AppPreference.getProfile().idDriver,
                            mLastLocation.latitude.toString(),
                            mLastLocation.longitude.toString(),
                            "",
                            "",
                            ""

                        )
                    )
                }
            }
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun getLocationDetail(latitude: Double, longitude: Double): MutableList<Address>? {
        val geocoder = Geocoder(this, Locale.getDefault())
        return try {
            geocoder.getFromLocation(latitude, longitude, 1)
        } catch (e: IOException) {
            null
        }
    }
    companion object {
        private const val PERMISSION_ID = 42

        private const val TAG = "Manual"
        private const val RC_SIGN_IN = 9001
    }
}