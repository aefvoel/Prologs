package id.toriqwah.project.view.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import id.toriqwah.project.R
import id.toriqwah.project.databinding.ActivityNotificationBinding
import id.toriqwah.project.helper.UtilityHelper
import id.toriqwah.project.model.*
import id.toriqwah.project.view.base.BaseActivity
import id.toriqwah.project.view.detail.DetailViewModel
import kotlinx.android.synthetic.main.fragment_history.*
import org.koin.android.ext.android.inject
import java.util.*


class NotificationActivity : BaseActivity() {

    private lateinit var binding: ActivityNotificationBinding
    private val viewModel by inject<DetailViewModel>()

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

        }
        setNav()

    }


    companion object {
        private const val TAG = "Notification"
        private const val RC_SIGN_IN = 9001
    }



}