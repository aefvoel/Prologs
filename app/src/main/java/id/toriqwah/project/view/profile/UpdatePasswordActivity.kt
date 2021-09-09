package id.toriqwah.project.view.profile

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import id.toriqwah.project.R
import id.toriqwah.project.databinding.ActivityUpdatePasswordBinding
import id.toriqwah.project.helper.UtilityHelper
import id.toriqwah.project.model.Update
import id.toriqwah.project.util.AppPreference
import id.toriqwah.project.view.base.BaseActivity
import kotlinx.android.synthetic.main.activity_update_password.*
import org.koin.android.ext.android.inject
import java.util.*


class UpdatePasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding
    private val viewModel by inject<ProfileViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_update_password)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        with(viewModel) {
            hideKeyBoard.observe(this@UpdatePasswordActivity, Observer {
                UtilityHelper.hideSoftKeyboard(this@UpdatePasswordActivity)
            })
            snackbarMessage.observe(this@UpdatePasswordActivity, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(this@UpdatePasswordActivity, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })
            isLoading.observe(this@UpdatePasswordActivity, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })
            logoutSuccess.observe(this@UpdatePasswordActivity, Observer {
                finish()
            })

        }
        setView()
    }

    private fun setView(){
        setNav()
        update.setOnClickListener {
            viewModel.updatePassword(Update(
                new_pass = no.text.toString(),
                confirm_pass = pass.text.toString(),
                driver_id = AppPreference.getProfile().idDriver
            ))
        }

    }

    companion object {
        private const val TAG = "UpdatePassword"
        private const val RC_SIGN_IN = 9001
    }


}