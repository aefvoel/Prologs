package id.prologs.driver.view.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.prologs.driver.model.Check
import id.prologs.driver.model.Login
import id.prologs.driver.model.Setting
import id.prologs.driver.model.Update
import id.prologs.driver.repository.UserRepository
import id.prologs.driver.util.AppPreference
import id.prologs.driver.util.SingleLiveEvent
import id.prologs.driver.view.base.BaseViewModel
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : BaseViewModel(){

    /**
     * Login
     */

    val email = MutableLiveData<String>()
    val password = MutableLiveData<String>()
    val loginSuccess = SingleLiveEvent<Unit>()
    val clickLogin = SingleLiveEvent<Unit>()


    fun onClickLogin(){
        clickLogin.call()
    }



    fun checkImei(login: Login) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.checkImei(login)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        login(Check(response.body.data.idDriver.toInt()))
                        AppPreference.putProfile(response.body.data)
                    } else {
                        snackbarMessage.value = response.body.message
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
                        AppPreference.putAttendance(response.body.id.toString())
                        loginSuccess.call()
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

    val data = MutableLiveData<Setting>()

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

    fun appSetting(update: Update) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.appSetting(update)) {
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