package id.prologs.driver.view.received

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.raodevs.touchdraw.TouchDrawView
import id.prologs.driver.R
import id.prologs.driver.databinding.ActivityReceivedBinding
import id.prologs.driver.helper.CameraHelper
import id.prologs.driver.helper.PermissionHelper
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.util.AppPreference
import id.prologs.driver.util.Constant
import id.prologs.driver.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_received.*
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


class ReceivedActivity : BaseActivity() {

    private lateinit var binding: ActivityReceivedBinding
    private val viewModel by inject<ReceivedViewModel>()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_received)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@ReceivedActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@ReceivedActivity)
            })
            snackbarMessage.observe(this@ReceivedActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@ReceivedActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@ReceivedActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })

            clickSubmit.observe(this@ReceivedActivity, Observer {
                prepareUploadImage()
            })
            updateSuccess.observe(this@ReceivedActivity, Observer {
                finish()
            })

        }
        setNav()
        setView()
    }

    private fun setView(){
        card_photo.setOnClickListener {
            requestCameraStoragePermission(true)
        }
        card_sign.setOnClickListener {
            openDialogSignature()
        }
        btn_submit.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setMessage("Are you sure want update delivery status?")
                .setPositiveButton("Yes") { dialog, which ->
                    prepareUploadImage()
                }
                .setNegativeButton(
                    "No"
                ) { dialog, _ ->
                    dialog.cancel()
                }
            builder.show()
        }
    }
    private fun openDialogSignature() {
        val sheetDialog = BottomSheetDialog(this)
        val v: View = LayoutInflater.from(this).inflate(R.layout.dialog_signature, null)
        val drawView = v.findViewById<View>(R.id.draw) as TouchDrawView
        drawView.setPaintColor(Color.BLACK)
        drawView.setBGColor(Color.LTGRAY)
        sheetDialog.setContentView(v)
        //sheetDialog.setCancelable(true);
        sheetDialog.show()
    }

    private fun requestCameraStoragePermission(isCamera: Boolean) {
        PermissionHelper.setPermissionListener(object :
            PermissionHelper.Companion.PermissionListener {
            override fun onPermissionGranted(isAllGranted: Boolean) {
                if (isAllGranted) {
                    if (isCamera) {
                        CameraHelper.launchCameraApp(
                            Constant.RequestCamera,
                            this@ReceivedActivity,
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
                        this@ReceivedActivity, "Camera dan Storage " +
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

            img_foto.setImageBitmap(image)

        }
    }



    private fun prepareUploadImage() {
        AppPreference.getImage()?.let {
            val file = createImageFile(this, it, 1)
            val imagesBody = file.asRequestBody("image/*".toMediaTypeOrNull())
            val photo = MultipartBody.Part.createFormData("file", file.name, imagesBody)
            val map: HashMap<String, RequestBody> = HashMap()
            map["id_driver"] = AppPreference.getProfile().idDriver.toRequestBody("text/plain".toMediaTypeOrNull())
            map["id_shipment"] = intent.extras?.getString("id_shipment").toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["id_order"] = intent.extras?.getString("id_order").toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["id_status"] = "25".toRequestBody("text/plain".toMediaTypeOrNull())
            map["received_by"] = receiver_name.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            map["note"] = note.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())

            viewModel.completeJob(map, photo)
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

        private const val TAG = "Received"
        private const val RC_SIGN_IN = 9001
    }


}