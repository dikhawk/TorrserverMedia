package com.dik.torrserverapi.server

sealed interface ServerCommands {
    interface InstalServer: ServerCommands
    interface StartServer: ServerCommands
    interface StopServer: ServerCommands
    interface RestartServer: ServerCommands
    interface CheckNewVersion: ServerCommands
    interface RestoreFromeBackUp: ServerCommands
}