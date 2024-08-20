package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import okio.FileSystem
import okio.Path.Companion.toPath

class RestoreServerFromBackUp {
    operator fun invoke(pathToBackupFile: String, pathToFile: String): Result<Unit, TorrserverError> {
        val fileSystem = FileSystem.SYSTEM
        val file = pathToFile.toPath()
        val backUpFile = pathToBackupFile.toPath()

        if (!fileSystem.exists(file))
            return Result.Error(TorrserverError.Server.FileNotExist("File not exist: $pathToFile"))

        fileSystem.delete(file)
        fileSystem.copy(source = backUpFile, target = file)

        return Result.Success(Unit)
    }
}