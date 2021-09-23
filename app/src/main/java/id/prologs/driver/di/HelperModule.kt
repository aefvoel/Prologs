package id.prologs.driver.di

import id.prologs.driver.helper.PermissionHelper
import id.prologs.driver.helper.UtilityHelper
import org.koin.dsl.module

val HelperModule = module {
    single { PermissionHelper() }
    single { UtilityHelper() }

}