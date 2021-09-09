package id.toriqwah.project.di

import id.toriqwah.project.repository.UserRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single { UserRepository(get()) }
}