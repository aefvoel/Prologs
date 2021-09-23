package id.prologs.driver.view.main

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import id.prologs.driver.R
import id.prologs.driver.databinding.ActivityNotificationBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.*
import id.prologs.driver.view.base.BaseActivity
import id.prologs.driver.view.detail.DetailViewModel
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