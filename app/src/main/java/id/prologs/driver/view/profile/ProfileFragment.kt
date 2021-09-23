package id.prologs.driver.view.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import id.prologs.driver.R
import id.prologs.driver.databinding.FragmentProfileBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.Logout
import id.prologs.driver.util.AppPreference
import id.prologs.driver.view.base.BaseFragment
import id.prologs.driver.view.splashscreen.SplashScreenActivity
import kotlinx.android.synthetic.main.fragment_profile.*
import org.koin.android.ext.android.inject

class ProfileFragment : BaseFragment(){

    private lateinit var binding: FragmentProfileBinding
    private val viewModel by inject<ProfileViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.vm = viewModel
        binding.lifecycleOwner = this.viewLifecycleOwner

        with(viewModel) {
            snackbarMessage.observe(viewLifecycleOwner, Observer {
                when (it) {
                    is Int -> UtilityHelper.snackbarLong(view_parent, getString(it))
                    is String -> UtilityHelper.snackbarLong(view_parent, it)
                }
            })
            networkError.observe(viewLifecycleOwner, Observer {
                UtilityHelper.snackbarLong(view_parent, getString(R.string.error_network))
            })

            isLoading.observe(viewLifecycleOwner, Observer { bool ->
                bool.let { loading ->
                    if (loading) {
                        showWaitingDialog()
                    } else {
                        hideWaitingDialog()
                    }
                }
            })
            logoutSuccess.observe(viewLifecycleOwner, Observer {
                AppPreference.deleteAll()
                val intent = Intent(context, SplashScreenActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                activity?.finish()
            })

        }
        setView()
    }


    private fun setView(){

        UtilityHelper.setImage(requireContext(), AppPreference.getProfile().pic, imageView)
        name.text = AppPreference.getProfile().fullname
        email.text = AppPreference.getProfile().email
        phone.text = AppPreference.getProfile().phone

        out.setOnClickListener {
            viewModel.logout(Logout(AppPreference.getProfile().attendanceId.toInt()))
        }
        update.setOnClickListener {
            startActivity(Intent(context, UpdatePasswordActivity::class.java))
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() = ProfileFragment()
    }
}