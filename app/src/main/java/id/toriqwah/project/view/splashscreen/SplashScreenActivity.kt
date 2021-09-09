package id.toriqwah.project.view.splashscreen

import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import id.toriqwah.project.R
import id.toriqwah.project.databinding.ActivitySplashScreenBinding
import id.toriqwah.project.helper.UtilityHelper
import id.toriqwah.project.view.base.BaseActivity
import id.toriqwah.project.view.login.LoginActivity
import kotlinx.android.synthetic.main.activity_splash_screen.*
import org.koin.android.ext.android.inject

class SplashScreenActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private val viewModel by inject<SplashScreenViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_screen)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@SplashScreenActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@SplashScreenActivity)
            })
            snackbarMessage.observe(this@SplashScreenActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@SplashScreenActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@SplashScreenActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })
            data.observe(this@SplashScreenActivity, Observer {
                setSplashScreen()
            })
        }
        viewModel.appSetting()
    }
    
    private fun setSplashScreen(){
        UtilityHelper.setImage(this, viewModel.data.value!!.splashScreen, splash)
        UtilityHelper.setImage(this, viewModel.data.value!!.logo, logo)
        btn_get.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}