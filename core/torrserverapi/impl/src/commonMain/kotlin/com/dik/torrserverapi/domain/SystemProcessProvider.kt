package com.dik.torrserverapi.domain

internal interface SystemProcessProvider {
    fun isProcessRunning(processName: String): Boolean
}