package com.dik.torrentlist.di

import org.koin.core.module.Module

internal expect fun platformModule(dependencies: TorrentListDependencies): Module