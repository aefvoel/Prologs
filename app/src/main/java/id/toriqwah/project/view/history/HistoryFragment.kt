package id.toriqwah.project.view.history

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.toriqwah.project.R
import id.toriqwah.project.adapter.TaskAdapter
import id.toriqwah.project.databinding.FragmentHistoryBinding
import id.toriqwah.project.helper.UtilityHelper
import id.toriqwah.project.model.Check
import id.toriqwah.project.model.Task
import id.toriqwah.project.util.AppPreference
import id.toriqwah.project.view.base.BaseFragment
import id.toriqwah.project.view.detail.DetailActivity
import kotlinx.android.synthetic.main.fragment_assigned.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_history.rv_assignment
import kotlinx.android.synthetic.main.fragment_history.swipe
import kotlinx.android.synthetic.main.fragment_history.view_parent
import org.koin.android.ext.android.inject

class HistoryFragment : BaseFragment(), TaskAdapter.Listener{

    private lateinit var binding: FragmentHistoryBinding
    private val viewModel by inject<HistoryViewModel>()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_history, container, false)
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
                        swipe.isRefreshing = false
                    }
                }
            })
            listTask.observe(viewLifecycleOwner, Observer {
                setListTask(it)
            })

        }
        swipe.setOnRefreshListener {
            viewModel.listCompletedTask(Check(AppPreference.getProfile().idDriver.toInt()))
        }
        viewModel.listCompletedTask(Check(AppPreference.getProfile().idDriver.toInt()))
    }

    private fun setListTask(list: ArrayList<Task>){
        rv_assignment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = TaskAdapter(context, list, this@HistoryFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = HistoryFragment()
    }

    override fun onItemClicked(data: Task) {
        startActivity(Intent(context, DetailActivity::class.java)
            .putExtra("id_order", data.idOrder)
            .putExtra("source", "3"))
    }
}