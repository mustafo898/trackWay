package dark.composer.trackway.data.di.module

import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module


@Module
abstract class ViewModelFactoryModule {
    @Binds
    abstract fun bindViewModelFactor(modelProviderFactory: DaggerViewModelFactory?): ViewModelProvider.Factory?
}