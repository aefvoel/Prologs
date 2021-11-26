package id.prologs.driver.view.login

import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import id.prologs.driver.R
import id.prologs.driver.databinding.ActivityLoginBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.Login
import id.prologs.driver.model.Update
import id.prologs.driver.util.AppPreference
import id.prologs.driver.view.base.BaseActivity
import id.prologs.driver.view.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.view_parent
import org.koin.android.ext.android.inject
import java.util.*


class LoginActivity : BaseActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel by inject<LoginViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@LoginActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@LoginActivity)
            })
            snackbarMessage.observe(this@LoginActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@LoginActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@LoginActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })

            clickLogin.observe(this@LoginActivity, Observer {
                setLogin()
            })
            data.observe(this@LoginActivity, Observer {
                UtilityHelper.setImage(this@LoginActivity, it.loginScreen, screen)
            })
            loginSuccess.observe(this@LoginActivity, Observer {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
            })

        }
        viewModel.appSetting(Update(
            driver_id = AppPreference.getProfile().idDriver
        ))
    }

    private fun setLogin(){
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(ContentValues.TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            AppPreference.putToken(token.toString())
            // Log and toast
            if (token != null) {
                Log.d(ContentValues.TAG, token)
            }

            viewModel.checkImei(Login(no.text.toString(),
                AppPreference.getToken(), pass.text.toString())
            )
        })

    }

    companion object {
        private const val TAG = "Login"
        private const val RC_SIGN_IN = 9001
    }


}