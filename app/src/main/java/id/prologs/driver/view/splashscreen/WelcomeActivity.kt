package id.prologs.driver.view.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import id.prologs.driver.R
import id.prologs.driver.databinding.ActivityWelcomeBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.Update
import id.prologs.driver.util.AppPreference
import id.prologs.driver.view.base.BaseActivity
import id.prologs.driver.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_welcome.*
import org.koin.android.ext.android.inject

class WelcomeActivity : BaseActivity() {

    private lateinit var binding: ActivityWelcomeBinding
    private val viewModel by inject<SplashScreenViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_welcome)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@WelcomeActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@WelcomeActivity)
            })
            snackbarMessage.observe(this@WelcomeActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@WelcomeActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@WelcomeActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })
            data.observe(this@WelcomeActivity, Observer {
                setSplashScreen()
            })
        }

        viewModel.appSetting(Update(
            driver_id = AppPreference.getProfile().idDriver
        ))

    }

    @SuppressLint("SetTextI18n")
    private fun setSplashScreen(){
        UtilityHelper.setImage(this, viewModel.data.value!!.splashScreen, splash)
        UtilityHelper.setImage(this, viewModel.data.value!!.logo, logo)
        Handler().postDelayed({
            when {

                AppPreference.isLogin() -> {
                    startActivity(Intent(this, MainActivity::class.java))
                }
                else -> {
                    startActivity(Intent(this, SplashScreenActivity::class.java))
                }
            }
            finish()
        },3000)
    }
}