package dark.composer.trackway.data.di.module

import dagger.Module
import dagger.android.ContributesAndroidInjector
import dark.composer.trackway.presentation.splash.SplashFragment
import dark.composer.trackway.presentation.travel.TravelFragment

@Module
public abstract class MainFragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun splashFragment(): SplashFragment

    @ContributesAndroidInjector
    abstract fun travelFragment(): TravelFragment
}