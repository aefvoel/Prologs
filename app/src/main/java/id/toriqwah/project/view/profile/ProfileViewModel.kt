package id.toriqwah.project.view.profile

import androidx.lifecycle.viewModelScope
import com.haroldadmin.cnradapter.NetworkResponse
import id.toriqwah.project.model.Logout
import id.toriqwah.project.model.Update
import id.toriqwah.project.repository.UserRepository
import id.toriqwah.project.util.SingleLiveEvent
import id.toriqwah.project.view.base.BaseViewModel
import kotlinx.coroutines.launch

class ProfileViewModel(private val userRepository: UserRepository) : BaseViewModel(){

    val logoutSuccess = SingleLiveEvent<Unit>()


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
    fun updatePassword(update: Update) {
        isLoading.value = true
        viewModelScope.launch {
            when (val response = userRepository.updatePassword(update)) {
                is NetworkResponse.Success -> {
                    isLoading.value = false
                    if (response.body.status){
                        snackbarMessage.value = response.body.message
                        logoutSuccess.call()
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
}