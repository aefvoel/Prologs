package id.toriqwah.project.view.splashscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.toriqwah.project.model.Setting
import id.toriqwah.project.repository.UserRepository
import id.toriqwah.project.view.base.BaseViewModel
import kotlinx.coroutines.launch

class SplashScreenViewModel(private val userRepository: UserRepository) : BaseViewModel() {

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
}