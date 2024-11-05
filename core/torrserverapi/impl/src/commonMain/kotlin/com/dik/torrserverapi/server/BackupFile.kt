package com.dik.torrserverapi.server

import co.touchlab.kermit.Logger
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import okio.FileSystem
import okio.Path.Companion.toPath


internal class BackupFile {
    val tag = "BackupFile:"

    operator fun invoke(
        pathToFile: String,
        pathToBackupFile: String
    ): Result<Unit, TorrserverError> {
        val fileSystem = FileSystem.SYSTEM
        val file = pathToFile.toPath()
        val backUpFile = pathToBackupFile.toPath()
        Logger.i("$tag started backup for file: $pathToFile")

        if (!fileSystem.exists(file)) {
            Logger.e("$tag File not exist: $pathToFile")
            return Result.Error(TorrserverError.Server.FileNotExist("File not exist: $pathToFile"))
        }

        fileSystem.delete(backUpFile)
        fileSystem.copy(source = file, target = backUpFile)

        Logger.i("$tag Success result. Created backup for file: $pathToFile,  saved to $pathToBackupFile")
        return Result.Success(Unit)
    }
}