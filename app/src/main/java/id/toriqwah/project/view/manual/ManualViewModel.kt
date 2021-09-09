package id.toriqwah.project.view.manual

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.toriqwah.project.model.Check
import id.toriqwah.project.model.Login
import id.toriqwah.project.model.Reason
import id.toriqwah.project.model.Update
import id.toriqwah.project.repository.UserRepository
import id.toriqwah.project.util.AppPreference
import id.toriqwah.project.util.SingleLiveEvent
import id.toriqwah.project.view.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ManualViewModel(private val userRepository: UserRepository) : BaseViewModel(){

    /**
     * Login
     */

    val updateSuccess = SingleLiveEvent<Unit>()
    val clickSubmit = SingleLiveEvent<Unit>()
    val clickReturn = SingleLiveEvent<Unit>()
    val clickPhoto = SingleLiveEvent<Unit>()
    val listReason = MutableLiveData<ArrayList<Reason>>()

    val name = MutableLiveData<String>()
    val address = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val customer = MutableLiveData<String>()
    val product = MutableLiveData<String>()
    val productDetails = MutableLiveData<String>()
    val owner = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val notes = MutableLiveData<String>()
    val lat = MutableLiveData<String>()
    val lon = MutableLiveData<String>()

    fun onClickSubmit(){
        clickSubmit.call()
    }
    fun onClickReturn(){
        clickReturn.call()
    }
    fun onClickPhoto(){
        clickPhoto.call()
    }



    fun completeJob(request: Map<String, RequestBody>, photo: MultipartBody.Part) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.completeJob(request, photo)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        updateSuccess.call()
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

    fun completeJobManual(request: Map<String, RequestBody>, photo: MultipartBody.Part) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.completeJobManual(request, photo)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        updateSuccess.call()
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
                        updateSuccess.call()
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