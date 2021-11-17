package id.prologs.driver.view.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import id.prologs.driver.R
import id.prologs.driver.adapter.NotificationAdapter
import id.prologs.driver.adapter.TaskAdapter
import id.prologs.driver.databinding.ActivityNotificationBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.*
import id.prologs.driver.util.AppPreference
import id.prologs.driver.util.SharedPreferenceUtil
import id.prologs.driver.view.base.BaseActivity
import id.prologs.driver.view.detail.DetailViewModel
import kotlinx.android.synthetic.main.activity_notification.*
import kotlinx.android.synthetic.main.fragment_assigned.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.rv_assignment
import kotlinx.android.synthetic.main.fragment_history.swipe
import kotlinx.android.synthetic.main.fragment_history.view_parent
import org.koin.android.ext.android.inject
import java.util.*


class NotificationActivity : BaseActivity(), NotificationAdapter.Listener {

    private lateinit var binding: ActivityNotificationBinding
    private val viewModel by inject<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_notification)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@NotificationActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@NotificationActivity)
            })
            snackbarMessage.observe(this@NotificationActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@NotificationActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@NotificationActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                        swipe.isRefreshing = false
                    }
                }
            })
            listNotif.observe(this@NotificationActivity, Observer {
                setListNotification(it)
            })
            updateSuccess.observe(this@NotificationActivity, Observer {
                viewModel.listNotification(Update(
                    driver_id = AppPreference.getProfile().idDriver
                ))
            })

        }
        setToolbar("Notification")
        viewModel.listNotification(Update(
            driver_id = AppPreference.getProfile().idDriver
        ))
        setView()

    }

    private fun setView(){
        swipe.setOnRefreshListener {
            viewModel.listNotification(Update(
                driver_id = AppPreference.getProfile().idDriver
            ))
        }
    }
    private fun setListNotification(list: ArrayList<Notification>){
        rv_notification.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = NotificationAdapter(context, list, this@NotificationActivity)
        }
    }

    private fun openDialogNotification(text: String) {
        val sheetDialog = BottomSheetDialog(this)
        val v: View = LayoutInflater.from(this).inflate(R.layout.dialog_notification, null)
        sheetDialog.setContentView(v)

        val detailText = v.findViewById<View>(R.id.detail) as TextView
        detailText.text = text

        sheetDialog.setCancelable(true)
        sheetDialog.show()
    }

    companion object {
        private const val TAG = "Notification"
        private const val RC_SIGN_IN = 9001
    }

    override fun onItemClicked(data: Notification) {
        viewModel.markNotification(Update(
            id_notification = data.idNotification
        ))
        openDialogNotification(data.detailMessage)
    }

    override fun onRemove(data: Notification) {
        viewModel.deleteNotification(Update(
            id_notification = data.idNotification
        ))
    }


}