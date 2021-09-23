package id.prologs.driver.view.assigned

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import id.prologs.driver.R
import id.prologs.driver.adapter.TaskAdapter
import id.prologs.driver.databinding.FragmentAssignedBinding
import id.prologs.driver.helper.UtilityHelper
import id.prologs.driver.model.Check
import id.prologs.driver.model.Task
import id.prologs.driver.util.AppPreference
import id.prologs.driver.view.base.BaseFragment
import id.prologs.driver.view.detail.DetailActivity
import kotlinx.android.synthetic.main.fragment_assigned.*
import org.koin.android.ext.android.inject

class AssignedFragment : BaseFragment(), TaskAdapter.Listener{

    private lateinit var binding: FragmentAssignedBinding
    private val viewModel by inject<AssignedViewModel>()
    var tabId = "New Task"


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_assigned, container, false)
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
                        swipe.isRefreshing = false
                        hideWaitingDialog()
                    }
                }
            })
            listTask.observe(viewLifecycleOwner, Observer {

                setListTask(it)
            })
            listRunningTask.observe(viewLifecycleOwner, Observer {

                setListRunningTask(it)
            })

        }
        viewModel.listNewTask(Check(AppPreference.getProfile().idDriver.toInt()))
        setView()
    }

    private fun setView(){

        segmented {

            // set initial checked segment (null by default)
            initialCheckedIndex = 0

            // init with segments programmatically without RadioButton as a child in xml
            initWithItems {
                // takes only list of strings
                listOf("New Task", "Running Task")
            }

            // notifies when segment was checked
            onSegmentChecked { segment ->
                tabId = segment.text.toString()
                when(segment.text){
                    "New Task" -> {
                        rv_assignment_run.visibility = View.GONE
                        rv_assignment.visibility = View.VISIBLE
                        viewModel.listNewTask(Check(AppPreference.getProfile().idDriver.toInt()))
                    }
                    "Running Task" -> {
                        rv_assignment_run.visibility = View.VISIBLE
                        rv_assignment.visibility = View.GONE
                        viewModel.listRunningTask(Check(AppPreference.getProfile().idDriver.toInt()))
                    }
                }
                Log.d("creageek:segmented", "Segment ${segment.text} checked")
            }
            // notifies when segment was unchecked
            onSegmentUnchecked { segment ->
                Log.d("creageek:segmented", "Segment ${segment.text} unchecked")
            }
            // notifies when segment was rechecked
            onSegmentRechecked { segment ->
                Log.d("creageek:segmented", "Segment ${segment.text} rechecked")
            }
        }

        swipe.setOnRefreshListener {
            when(tabId){
                "New Task" -> {
                    rv_assignment_run.visibility = View.GONE
                    rv_assignment.visibility = View.VISIBLE
                    viewModel.listNewTask(Check(AppPreference.getProfile().idDriver.toInt()))
                }
                "Running Task" -> {
                    rv_assignment_run.visibility = View.VISIBLE
                    rv_assignment.visibility = View.GONE
                    viewModel.listRunningTask(Check(AppPreference.getProfile().idDriver.toInt()))
                }
            }
        }
    }

    private fun setListTask(list: ArrayList<Task>){
        rv_assignment.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = TaskAdapter(context, list, this@AssignedFragment)
        }
    }

    private fun setListRunningTask(list: ArrayList<Task>){
        rv_assignment_run.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = TaskAdapter(context, list, this@AssignedFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AssignedFragment()
    }

    override fun onItemClicked(data: Task) {
        when(tabId){
            "New Task" -> {
                startActivity(Intent(context, DetailActivity::class.java)
                    .putExtra("id_order", data.idOrder)
                    .putExtra("source", "1"))
            }
            "Running Task" -> {
                startActivity(Intent(context, DetailActivity::class.java)
                    .putExtra("id_order", data.idOrder)
                    .putExtra("source", "2"))
            }
        }
    }
}