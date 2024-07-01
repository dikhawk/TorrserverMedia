package com.dik.torrserverapi.server.mappers

import com.dik.torrserverapi.model.Asset
import com.dik.torrserverapi.model.Release
import com.dik.torrserverapi.server.response.AssetResponse
import com.dik.torrserverapi.server.response.ReleaseResponse

internal fun ReleaseResponse.mapRelease(): Release =
    Release(url = url, tagName = tagName, publishedAt = publishedAt, assets = assets.mapListAsset())

internal fun AssetResponse.mapAsset(): Asset =
    Asset(name = name, browserDownloadUrl = browserDownloadUrl, updatedAt = updatedAt)

internal fun List<AssetResponse>.mapListAsset(): List<Asset> = map { it.mapAsset() }