package dark.composer.trackway.data.di.module

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import dark.composer.trackway.data.scopes.ViewModelKey
import dark.composer.trackway.presentation.travel.TravelViewModel

@Module
abstract class ViewModelsModule {

    @Binds
    @IntoMap
    @ViewModelKey(TravelViewModel::class)
    abstract fun splashViewModel(travelViewModel : TravelViewModel): ViewModel

}