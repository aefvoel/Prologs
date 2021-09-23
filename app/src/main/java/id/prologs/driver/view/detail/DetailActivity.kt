package id.prologs.driver.view.detail

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.prologs.driver.R
import id.prologs.driver.adapter.ShipmentAdapter
import id.prologs.driver.databinding.ActivityDetailBinding
import id.prologs.driver.helper.CameraHelper
import id.prologs.driver.helper.PermissionHelper
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.*
import id.prologs.driver.util.AppPreference
import id.prologs.driver.util.Constant
import id.prologs.driver.view.base.BaseActivity
import id.prologs.driver.view.received.ReceivedActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_detail.rv_assignment
import kotlinx.android.synthetic.main.activity_detail.swipe
import kotlinx.android.synthetic.main.activity_detail.view_parent
import kotlinx.android.synthetic.main.fragment_history.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.koin.android.ext.android.inject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.*


class DetailActivity : BaseActivity(), ShipmentAdapter.Listener {

    private lateinit var binding: ActivityDetailBinding
    private val viewModel by inject<DetailViewModel>()
    private var idOrder = ""
    private var idReason = ""
    private var idShipment = ""

    private var array = arrayListOf<String>()
    private var arrayId = arrayListOf<String>()

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@DetailActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@DetailActivity)
            })
            snackbarMessage.observe(this@DetailActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@DetailActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@DetailActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                        swipe.isRefreshing = false
                    }
                }
            })

            data.observe(this@DetailActivity, Observer {
                setView(it)
            })

            listReason.observe(this@DetailActivity, Observer {
                for (index in it){
                    array.add(index.reason)
                    arrayId.add(index.id)
                }
            })
            responseSuccess.observe(this@DetailActivity, Observer {
                viewModel.detailTask(Detail(intent.extras?.getString("id_order")!!.toInt(),
                    intent.extras?.getString("source")!!.toInt()))
                Toast.makeText(
                        this@DetailActivity, "Success", Toast.LENGTH_LONG
                ).show()
            })

        }
        setNav()

        viewModel.detailTask(Detail(intent.extras?.getString("id_order")!!.toInt(),
            intent.extras?.getString("source")!!.toInt()))
        viewModel.getListReason()

    }

    override fun onResume() {
        super.onResume()
        viewModel.detailTask(Detail(intent.extras?.getString("id_order")!!.toInt(),
            intent.extras?.getString("source")!!.toInt()))
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun setView(data: DetailResponse) {
        swipe.setOnRefreshListener {
            viewModel.detailTask(Detail(intent.extras?.getString("id_order")!!.toInt(),
                intent.extras?.getString("source")!!.toInt()))
        }
        idOrder = data.order.idOrder
        if (data.order.isMultidrop == "0"){
            type.backgroundTintList = getColorStateList(R.color.hard_grey)
        }
        else {
            type.backgroundTintList = getColorStateList(R.color.colorRed)
        }

        rv_assignment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = ShipmentAdapter(context, data.shipment, data.shipper, this@DetailActivity)
        }
    }

    companion object {
        private const val TAG = "Detail"
        private const val RC_SIGN_IN = 9001
    }

    override fun onReturnClicked(id: String, status: String) {
        idShipment = id
        val buildr = AlertDialog.Builder(this)
        buildr.setTitle("Select Reason")
        buildr.setItems(array.toTypedArray()) { dialg, item -> // Do something with the selection
            idReason = arrayId[item]
            requestCameraStoragePermission(true)

            dialg.dismiss()
        }
        buildr.show()
    }

    override fun onDeliverClicked(id: String, status: String) {

        if (status == "24") {
            val intent = Intent(this, ReceivedActivity::class.java)
            intent.putExtra("id_order", idOrder)
            intent.putExtra("id_shipment", id)
            startActivity(intent)
        } else {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure want update delivery status?")
                    .setPositiveButton("Yes") { dialog, which ->
                        viewModel.updateTask(Update(
                                idOrder,
                                id,
                                AppPreference.getProfile().idDriver,
                                "24"
                        ))
                    }
                    .setNegativeButton(
                            "No"
                    ) { dialog, _ ->
                        dialog.cancel()
                    }
                    .setNeutralButton(
                            "Not Pickup"
                    ) { dialog, which ->

                        val buildr = AlertDialog.Builder(this)
                        buildr.setTitle("Select Reason")
                        buildr.setItems(array.toTypedArray()) { dialg, item -> // Do something with the selection
                            viewModel.updateTask(Update(
                                    idOrder,
                                    id,
                                    AppPreference.getProfile().idDriver,
                                    "99",
                                    "",
                                    "",
                                    arrayId[item]
                            ))

                            dialg.dismiss()
                        }
                        buildr.show()
            }
            builder.show()

        }

    }

    private fun requestCameraStoragePermission(isCamera: Boolean) {
        PermissionHelper.setPermissionListener(object :
                PermissionHelper.Companion.PermissionListener {
            override fun onPermissionGranted(isAllGranted: Boolean) {
                if (isAllGranted) {
                    if (isCamera) {
                        CameraHelper.launchCameraApp(
                                Constant.RequestCamera,
                                this@DetailActivity,
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
                            this@DetailActivity, "Camera dan Storage " +
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

//            img_foto.setImageBitmap(image)

            prepareUploadImage()

        }
    }

    private fun prepareUploadImage() {
        AppPreference.getImage()?.let {
            val file = createImageFile(this, it, 1)
            val imagesBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photo = MultipartBody.Part.createFormData("file", file.name, imagesBody)
            val map: HashMap<String, RequestBody> = HashMap()
            map["id_driver"] = AppPreference.getProfile().idDriver.toRequestBody("text/plain".toMediaTypeOrNull())
            map["id_shipment"] = idShipment.toRequestBody("text/plain".toMediaTypeOrNull())
            map["id_order"] = idOrder.toRequestBody("text/plain".toMediaTypeOrNull())
            map["id_status"] = "26".toRequestBody("text/plain".toMediaTypeOrNull())
            map["received_by"] = "".toRequestBody("text/plain".toMediaTypeOrNull())
            map["note"] = "".toRequestBody("text/plain".toMediaTypeOrNull())
            map["reason_id"] = idReason.toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.updateTaskReturn(map, photo)
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


}