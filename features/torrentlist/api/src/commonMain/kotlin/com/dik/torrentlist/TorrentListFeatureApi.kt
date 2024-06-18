package com.dik.torrentlist

import com.dik.moduleinjector.BaseApi

interface TorrentListFeatureApi : BaseApi {
    fun start(): TorrentListEntry
}