package id.prologs.driver.view.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.prologs.driver.model.*
import id.prologs.driver.repository.UserRepository
import id.prologs.driver.util.AppPreference
import id.prologs.driver.util.SingleLiveEvent
import id.prologs.driver.view.base.BaseViewModel
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : BaseViewModel(){

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
    val logoutSuccess = SingleLiveEvent<Unit>()
    val loginSuccess = SingleLiveEvent<Unit>()

    val data = MutableLiveData<Setting>()


    fun onClickSubmit(){
        clickSubmit.call()
    }
    fun onClickReturn(){
        clickReturn.call()
    }
    fun onClickPhoto(){
        clickPhoto.call()
    }



    fun trackDriver(track: Track) {
        viewModelScope.launch {
            when (val response = userRepository.trackDriver(track)) {
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

    fun logout(out: Logout) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.checkOut(out)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        logoutSuccess.call()
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

    fun login(check: Check) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.checkIn(check)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        AppPreference.putLogin(true)
                        loginSuccess.call()
                        AppPreference.putAttendance(response.body.id.toString())
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

    fun appSetting() {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.appSetting()) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status) {
                        data.value = response.body.data!!
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