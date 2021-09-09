package id.toriqwah.project.view.detail

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.camerakit.CameraKit
import id.toriqwah.project.R
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class CameraActivity : AppCompatActivity() {

    var bundle: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        bundle = getIntent().extras
        if (bundle!!.getBoolean("back", false)) {
            camera.facing = CameraKit.FACING_BACK
            btn_front.visibility = View.VISIBLE
            btn_back.visibility = View.GONE
        } else {
            btn_front.visibility = View.GONE
            btn_back.visibility = View.VISIBLE
        }
        btn_front.setOnClickListener {
            camera.facing = CameraKit.FACING_FRONT
            camera.visibility = View.GONE
            camera.visibility = View.VISIBLE
        }
        btn_back.setOnClickListener {
            camera.facing = CameraKit.FACING_BACK
            btn_front.visibility = View.VISIBLE
            btn_back.visibility = View.GONE
        }
        btn_camera.setOnClickListener {
            takePhoto()
        }
    }

    private fun takePhoto(){
        camera.captureImage { cameraKitView, capturedImage -> //String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Mister Kirim/";
            val f =
                File(Environment.getExternalStorageDirectory(), "Mister Kirim")
            if (!f.exists()) {
                f.mkdirs()
            }
            Log.e(
                "job_id: ",
                bundle!!.getString("job_id") + "receiver name:" + bundle!!.getString("receiver_name")
            )
            val savedPhoto = File(f, bundle!!.getString("job_id") + ".jpg")
            Log.e("savedphoto: ", "file:$savedPhoto")
            try {
                val outputStream = FileOutputStream(savedPhoto.path)
                outputStream.write(capturedImage)
                outputStream.close()
                val intent = Intent()
                intent.putExtras(bundle!!)
                setResult(RESULT_OK, intent)
                finish()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    override fun onStart() {
        super.onStart()
        camera.onStart()
    }

    override fun onResume() {
        super.onResume()
        camera.onResume()
    }

    override fun onPause() {
        super.onPause()
        camera.onPause()
    }

    override fun onStop() {
        super.onStop()
        camera.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        camera.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}