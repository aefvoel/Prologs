package id.toriqwah.project.view.base

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import id.toriqwah.project.R
import kotlinx.android.synthetic.main.toolbar.*

open class BaseFragment : Fragment() {

    private lateinit var dialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDialog()
    }

    fun setToolbar(title: String) {
        toolbar_title.text = title
    }

    fun setNavigation(){
        toolbar_back.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    fun addFragment(fragment: Fragment) {
        requireActivity().supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out,android.R.anim.fade_in,android.R.anim.fade_out)
                .replace(R.id.content, fragment, fragment.javaClass.simpleName)
                .addToBackStack(null)
                .commit()
    }

    private fun initDialog() {
        dialog = Dialog(requireContext())
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setContentView(R.layout.dialog_waiting)
        dialog.setCancelable(false)
    }

    fun showWaitingDialog() {
        if (!dialog.isShowing) {
            dialog.show()
        }
    }

    fun hideWaitingDialog() {
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog.isShowing) {
            dialog.dismiss()
        }
    }
}