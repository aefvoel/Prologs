package id.prologs.driver.view.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.prologs.driver.model.*
import id.prologs.driver.repository.UserRepository
import id.prologs.driver.util.SingleLiveEvent
import id.prologs.driver.view.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class DetailViewModel(private val userRepository: UserRepository) : BaseViewModel() {

    /**
     * Login
     */

    val data = MutableLiveData<DetailResponse>()
    val isMulti = MutableLiveData<String>()
    val listReason = MutableLiveData<ArrayList<Reason>>()
    val responseSuccess = SingleLiveEvent<Unit>()


    fun detailTask(detail: Detail) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.detailTask(detail)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status) {
                        data.value = response.body
                        if (data.value!!.order.isMultidrop == "0") isMulti.value = "Single Drop"
                        else isMulti.value = "Multi Drop"
                    } else {
                        snackbarMessage.value = response.body.status
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

    fun updateTask(update: Update) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.updateTask(update)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status) {
                        responseSuccess.call()
                    } else {
                        snackbarMessage.value = response.body.status
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

    fun updateTaskReturn(request: Map<String, RequestBody>, photo: MultipartBody.Part) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.updateTaskRetur(request, photo)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        responseSuccess.call()
                    } else {
                        snackbarMessage.value = response.body.status
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

    fun getListReason() {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.listReason()) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status) {
                        listReason.value = response.body.data!!
                    } else {
                        snackbarMessage.value = response.body.status
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