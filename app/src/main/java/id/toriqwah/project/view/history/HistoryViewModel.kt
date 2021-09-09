package id.toriqwah.project.view.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.toriqwah.project.model.Check
import id.toriqwah.project.model.Task
import id.toriqwah.project.repository.UserRepository
import id.toriqwah.project.view.base.BaseViewModel
import kotlinx.coroutines.launch

class HistoryViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    val listTask = MutableLiveData<ArrayList<Task>>()
    val size = MutableLiveData<String>()
    val sum = MutableLiveData<Int>()

    fun listCompletedTask(check: Check) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.listCompletedTask(check)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        listTask.value = response.body.data!!
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