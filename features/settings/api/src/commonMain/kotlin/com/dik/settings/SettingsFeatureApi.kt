package com.dik.settings

import com.dik.moduleinjector.BaseApi

interface SettingsFeatureApi: BaseApi {
    fun start(): SettingsEntry
}