//package dark.composer.trackway.data.di.component
//
//import android.app.Application
//import dagger.BindsInstance
//import dagger.Component
//import dagger.android.AndroidInjector
//import dagger.android.support.AndroidSupportInjectionModule
//import dark.composer.trackway.app.App
//import dark.composer.trackway.data.di.module.ActivityBuildersModule
//import dark.composer.trackway.data.di.module.AppModule
//import dark.composer.trackway.data.di.module.ViewModelFactoryModule
//import javax.inject.Singleton
//
//@Singleton
//@Component(
//    modules = [
//        AndroidSupportInjectionModule::class,
//        AppModule::class,
//        ActivityBuildersModule::class,
//        ViewModelFactoryModule::class,
//    ]
//)
//interface AppComponent : AndroidInjector<App> {
//
//    @Component.Builder
//    interface Builder {
//        @BindsInstance
//        fun application(application: Application): Builder
//        fun build(): AppComponent
//    }
//}
