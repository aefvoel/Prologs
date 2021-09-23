package id.prologs.driver.di

import id.prologs.driver.repository.UserRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single { UserRepository(get()) }
}