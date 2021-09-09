package id.toriqwah.project.view.assigned

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.toriqwah.project.model.Check
import id.toriqwah.project.model.Task
import id.toriqwah.project.repository.UserRepository
import id.toriqwah.project.util.SingleLiveEvent
import id.toriqwah.project.view.base.BaseViewModel
import kotlinx.coroutines.launch

class AssignedViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    val listTask = MutableLiveData<ArrayList<Task>>()
    val listRunningTask = MutableLiveData<ArrayList<Task>>()
    val size = MutableLiveData<String>()
    val sum = MutableLiveData<Int>()
    val response = SingleLiveEvent<Unit>()

    fun listNewTask(check: Check) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.listNewTask(check)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        listTask.value = response.body.data!!
                        size.value = "You have ${listTask.value!!.size} assigned tasks."
                        sum.value = listTask.value!!.size
                    } else {
                        size.value = response.body.info.title + " " + response.body.info.message
                    }
                }
                is NetworkResponse.ServerError -> {
                    isLoading.value = false
                    snackbarMessage.value = response.body?.message
                }
                is NetworkResponse.NetworkError -> {
                    isLoading.value = false
                    networkError.value = response.error.message.toString()
                    snackbarMessage.value = response.error.message.toString()

                }
            }
        }
    }
    fun listRunningTask(check: Check) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.listRunningTask(check)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        listRunningTask.value = response.body.data!!
                        size.value = "You have ${listTask.value!!.size} assigned tasks."
                        sum.value = listTask.value!!.size
                    } else {
                        size.value = response.body.info.title + "\n" + response.body.info.message
                    }
                }
                is NetworkResponse.ServerError -> {
                    isLoading.value = false
                    snackbarMessage.value = response.body?.message
                }
                is NetworkResponse.NetworkError -> {
                    isLoading.value = false
                    networkError.value = response.error.message.toString()
                    snackbarMessage.value = response.error.message.toString()

                }
            }
        }
    }
}