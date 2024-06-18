package com.dik.torrserverapi.di

import com.dik.common.AppDispatchers
import com.dik.torrserverapi.MagnetApiImpl
import com.dik.torrserverapi.TorrentApiImpl
import com.dik.torrserverapi.TorrserverStuffApiImpl
import com.dik.torrserverapi.data.MagnetApi
import com.dik.torrserverapi.data.TorrentApi
import com.dik.torrserverapi.data.TorrserverStuffApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

internal fun torrserverModule(appDispatchers: AppDispatchers) = module {
    single<MagnetApi> { MagnetApiImpl() }
    single<TorrentApi> { TorrentApiImpl() }
    single<TorrserverStuffApi> { TorrserverStuffApiImpl() }
    single<AppDispatchers> { appDispatchers }
    single<CoroutineScope> {
        CoroutineScope(SupervisorJob() + appDispatchers.mainDispatcher())
    }
}