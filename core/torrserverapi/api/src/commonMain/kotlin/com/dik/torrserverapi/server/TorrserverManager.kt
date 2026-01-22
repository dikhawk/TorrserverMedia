package com.dik.torrserverapi.server

import kotlinx.coroutines.flow.StateFlow

interface TorrserverManager {

    fun observeTorserverStatus(): StateFlow<TorrserverStatus>

    fun executeCommand(command: TorrserverCommands)
}