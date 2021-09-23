package id.prologs.driver.view.received

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.prologs.driver.model.Reason
import id.prologs.driver.repository.UserRepository
import id.prologs.driver.util.SingleLiveEvent
import id.prologs.driver.view.base.BaseViewModel
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ReceivedViewModel(private val userRepository: UserRepository) : BaseViewModel(){

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

}