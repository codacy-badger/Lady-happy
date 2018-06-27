package com.egoriku.ladyhappy.di.application.module

import com.egoriku.core.di.ApplicationScope
import com.egoriku.core.di.utils.INavigationHolder
import com.egoriku.core.di.utils.IRouter
import com.egoriku.ladyhappy.di.tools.NavigationHolderImpl
import com.egoriku.ladyhappy.di.tools.RouterImpl
import dagger.Module
import dagger.Provides
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Router

@Module
class NavigationModule {

    private var cicerone: Cicerone<Router> = Cicerone.create()

    @ApplicationScope
    @Provides
    fun provideRouter(): IRouter = RouterImpl(cicerone.router)

    @ApplicationScope
    @Provides
    fun provideNavigationHolder(): INavigationHolder = NavigationHolderImpl(cicerone.navigatorHolder)
}