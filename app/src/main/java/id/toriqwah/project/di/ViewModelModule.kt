package id.toriqwah.project.di

import id.toriqwah.project.view.assigned.AssignedViewModel
import id.toriqwah.project.view.detail.DetailViewModel
import id.toriqwah.project.view.history.HistoryViewModel
import id.toriqwah.project.view.login.LoginViewModel
import id.toriqwah.project.view.main.MainViewModel
import id.toriqwah.project.view.manual.ManualViewModel
import id.toriqwah.project.view.profile.ProfileViewModel
import id.toriqwah.project.view.received.ReceivedViewModel
import id.toriqwah.project.view.splashscreen.SplashScreenViewModel
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