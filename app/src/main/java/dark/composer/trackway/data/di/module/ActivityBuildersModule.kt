package dark.composer.trackway.data.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dark.composer.trackway.presentation.MainActivity

@Module
abstract class ActivityBuildersModule {
    @ContributesAndroidInjector(modules = [ViewModelsModule::class, MainFragmentBuildersModule::class])
    abstract fun contributeMainActivity(): MainActivity
}