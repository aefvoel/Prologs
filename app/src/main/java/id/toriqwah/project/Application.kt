package id.toriqwah.project

import android.app.Application
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.orhanobut.hawk.Hawk
import id.toriqwah.project.di.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.EmptyLogger
import org.koin.core.logger.Level

class Application: Application() {

    override fun onCreate() {
        super.onCreate()
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        Hawk.init(this).build()
        startKoin()
    }

    private fun startKoin() {
        startKoin {
            if (BuildConfig.DEBUG) androidLogger(Level.ERROR) else EmptyLogger()
            androidContext(this@Application)
            modules(
                listOf(
                    CoreModule,
                    HelperModule,
                    NetworkModule,
                    RepositoryModule,
                    ViewModelModule
                )
            )
        }
    }
}