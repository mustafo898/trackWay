package dark.composer.trackway.data.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import dark.composer.trackway.data.utils.SharedPref
import javax.inject.Singleton

@Module()
object AppModule {

    @Singleton
    @Provides
    fun provideShared(application: Application) = SharedPref(application.applicationContext)
}