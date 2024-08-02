package com.dik.torrserverapi.cmd

interface ServerCommands {
    fun startServer(pathToServerFile: String)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object KmpServerCommands: ServerCommands