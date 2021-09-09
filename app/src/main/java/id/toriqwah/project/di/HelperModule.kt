package id.toriqwah.project.di

import id.toriqwah.project.helper.PermissionHelper
import id.toriqwah.project.helper.UtilityHelper
import org.koin.dsl.module

val HelperModule = module {
    single { PermissionHelper() }
    single { UtilityHelper() }

}