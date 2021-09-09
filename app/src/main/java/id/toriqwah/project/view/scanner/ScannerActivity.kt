package id.toriqwah.project.view.scanner

import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.google.zxing.Result
import id.toriqwah.project.util.AppPreference
import id.toriqwah.project.util.Constant
import id.toriqwah.project.view.base.BaseActivity
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.json.JSONException
import org.json.JSONObject

class ScannerActivity : BaseActivity(), ZXingScannerView.ResultHandler {

    var mScannerView: ZXingScannerView? = null
    var loading: ProgressDialog? = null

    override fun onCreate(state: Bundle?) {
        super.onCreate(state)
        mScannerView = ZXingScannerView(this) // Programmatically initialize the scanner view
        setContentView(mScannerView) // Set the scanner view as the content view
        loading = ProgressDialog(this)
        loading!!.setTitle("Loading")
        loading!!.setMessage("Please wait..")
        loading!!.isIndeterminate = true
        loading!!.setCancelable(false)
    }

    override fun onResume() {
        super.onResume()
        mScannerView!!.setResultHandler(this) // Register ourselves as a handler for scan results.
        mScannerView!!.startCamera() // Start camera on resume
    }

    override fun onPause() {
        super.onPause()
        mScannerView!!.stopCamera() // Stop camera on pause
    }

    override fun handleResult(rawResult: Result) {
        loading!!.show()
        pickup(rawResult.text)
    }

    private fun pickup(awb: String) {
        AndroidNetworking.post(Constant.BASE_URL + "driver/scan_pickup")
            .addHeaders("API-KEY", Constant.API_KEY)
            .addBodyParameter("awb", awb)
            .addBodyParameter("driver_id", AppPreference.getProfile().idDriver)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    loading!!.hide()
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this@ScannerActivity)
                    try {
                        if (response.getBoolean("status")) {
                            builder.setTitle("Success")
                                .setMessage(response.getString("msg"))
                        } else {
                            builder.setTitle("Error")
                                .setMessage(response.getString("msg"))
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                    builder.setCancelable(false)
                    builder.setPositiveButton("Close",
                        DialogInterface.OnClickListener { dialog, which ->
                            mScannerView!!.resumeCameraPreview(this@ScannerActivity)
                            dialog.dismiss()
                        })
                    builder.show()
                }

                override fun onError(anError: ANError) {
                    Log.e("AAA", anError.errorBody)
                }
            })
    }

}