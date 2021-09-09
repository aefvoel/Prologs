package id.toriqwah.project.view.manual

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import id.toriqwah.project.R
import id.toriqwah.project.databinding.ActivityManualBinding
import id.toriqwah.project.helper.CameraHelper
import id.toriqwah.project.helper.PermissionHelper
import id.toriqwah.project.helper.UtilityHelper
import id.toriqwah.project.model.Update
import id.toriqwah.project.util.AppPreference
import id.toriqwah.project.util.Constant
import id.toriqwah.project.view.base.BaseActivity
import id.toriqwah.project.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_manual.*
import kotlinx.android.synthetic.main.activity_manual.awb
import kotlinx.android.synthetic.main.activity_manual.img_foto
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.util.*


class ManualActivity : BaseActivity() {

    private lateinit var binding: ActivityManualBinding
    private val viewModel by inject<ManualViewModel>()
    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var array = arrayListOf<String>()
    private var arrayId = arrayListOf<String>()

    private var idReason = ""

    private var mLastLocation: Location? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_manual)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@ManualActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@ManualActivity)
            })
            snackbarMessage.observe(this@ManualActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@ManualActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@ManualActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })

            clickSubmit.observe(this@ManualActivity, Observer {
                prepareUploadImage()
            })
            clickReturn.observe(this@ManualActivity, Observer {
                val buildr = AlertDialog.Builder(this@ManualActivity)
                buildr.setTitle("Select Reason")
                buildr.setItems(array.toTypedArray()) { dialg, item -> // Do something with the selection

                    idReason = arrayId[item]

                    prepareUploadImage()

                    dialg.dismiss()
                }
                buildr.show()
            })
            listReason.observe(this@ManualActivity, Observer {
                for (index in it){
                    array.add(index.reason)
                    arrayId.add(index.id)
                }
            })

        }
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLastLocation()
        viewModel.getListReason()
        img_foto.setOnClickListener {
            requestCameraStoragePermission(true)
        }
        setNav()

    }

    private fun requestCameraStoragePermission(isCamera: Boolean) {
        PermissionHelper.setPermissionListener(object :
                PermissionHelper.Companion.PermissionListener {
            override fun onPermissionGranted(isAllGranted: Boolean) {
                if (isAllGranted) {
                    if (isCamera) {
                        CameraHelper.launchCameraApp(
                                Constant.RequestCamera,
                                this@ManualActivity,
                                null
                        )
                    } else {
                        val galleyIntent = Intent(
                                Intent.ACTION_PICK,
                                MediaStore.Images.Media.INTERNAL_CONTENT_URI
                        )
                        galleyIntent.type = "image/*"
                        startActivityForResult(galleyIntent, Constant.RequestGallery)
                    }
                } else {
                    Toast.makeText(
                            this@ManualActivity, "Camera dan Storage " +
                            "permission dibutuhkan!", Toast.LENGTH_LONG
                    ).show()
                }
            }
        })
        PermissionHelper.requestPermission(
                this, listOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        )
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                Constant.RequestCamera -> {
                    val photoUri = CameraHelper.retrievePhotoUriResult(this, null)
                    photoUri?.let {
                        addImage(it)
                    }
                }
                Constant.RequestGallery -> {
                    if (data != null) {
                        val imageUri = data.data
                        imageUri?.let {
                            addImage(it)
                        }
                    }
                }

            }
        }
    }

    private fun addImage(uri: Uri) {
        contentResolver.openInputStream(uri)?.let {
            val image = BitmapFactory.decodeStream(it)
            AppPreference.putImage(UtilityHelper.reduceImageSize(image))

            img_foto.setImageBitmap(UtilityHelper.reduceImageSize(image))

        }
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
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1

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
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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


    private fun prepareUploadImage() {
        AppPreference.getImage()?.let {
            val file = createImageFile(this, it, 1)
            val imagesBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photo = MultipartBody.Part.createFormData("file", file.name, imagesBody)
            val map: HashMap<String, RequestBody> = HashMap()
            map["driver_id"] = AppPreference.getProfile().idDriver.toRequestBody("text/plain".toMediaTypeOrNull())
            map["address_detail"] = viewModel.address.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["address_consigne"] = suggestion.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["latitude"] = viewModel.lat.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["longitude"] = viewModel.lon.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["item"] = description.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["consigne_phone"] = "".toRequestBody("text/plain".toMediaTypeOrNull())
            map["consigne_name"] = viewModel.phone.value.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["jobs_number"] = awb.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["job_status_id"] = "4".toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.completeJobManual(map, photo)
        }
    }
    private fun createImageFile(context: Context, bitmap: Bitmap, type: Int): File {
        val file: File = if (type == 0) {
            File(
                    context.cacheDir, System.currentTimeMillis().toString() + ".jpg"
            )
        } else {
            File(
                    context.cacheDir, System.currentTimeMillis().toString() + ".jpg"
            )
        }

        val bos = ByteArrayOutputStream()

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)

        val bitmapData: ByteArray = bos.toByteArray()

        val fos = FileOutputStream(file)
        fos.write(bitmapData)
        fos.flush()
        fos.close()

        return file
    }


    companion object {
        private const val PERMISSION_ID = 42

        private const val TAG = "Manual"
        private const val RC_SIGN_IN = 9001
    }


}