package id.prologs.driver.di

import id.prologs.driver.view.assigned.AssignedViewModel
import id.prologs.driver.view.detail.DetailViewModel
import id.prologs.driver.view.history.HistoryViewModel
import id.prologs.driver.view.login.LoginViewModel
import id.prologs.driver.view.main.MainViewModel
import id.prologs.driver.view.manual.ManualViewModel
import id.prologs.driver.view.profile.ProfileViewModel
import id.prologs.driver.view.received.ReceivedViewModel
import id.prologs.driver.view.splashscreen.SplashScreenViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val ViewModelModule = module {
    viewModel { LoginViewModel(get()) }
    viewModel { HistoryViewModel(get()) }
    viewModel { AssignedViewModel(get()) }
    viewModel { DetailViewModel(get()) }
    viewModel { ManualViewModel(get()) }
    viewModel { MainViewModel(get()) }
    viewModel { ReceivedViewModel(get()) }
    viewModel { SplashScreenViewModel(get()) }
    viewModel { ProfileViewModel(get()) }
}