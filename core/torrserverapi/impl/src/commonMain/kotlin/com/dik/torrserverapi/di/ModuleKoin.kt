package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin

abstract class ModuleKoin {
    companion object {
        fun init(appDispatchers: AppDispatchers) {
            //TODO Проверить выгрузку модуля, после закрытия экрана, а затем повторного открытия
            loadKoinModules(torrserverModule(appDispatchers))
        }
    }
}