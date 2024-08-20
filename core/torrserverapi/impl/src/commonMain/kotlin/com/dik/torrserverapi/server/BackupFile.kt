package com.dik.torrserverapi.server

import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import okio.FileSystem
import okio.Path.Companion.toPath


internal class BackupFile {
    operator fun invoke(pathToFile: String, pathToBackupFile: String): Result<Unit, TorrserverError> {
        val fileSystem = FileSystem.SYSTEM
        val file = pathToFile.toPath()
        val backUpFile = pathToBackupFile.toPath()

        if (!fileSystem.exists(file))
            return Result.Error(TorrserverError.Server.FileNotExist("File not exist: $pathToFile"))

        fileSystem.delete(backUpFile)
        fileSystem.copy(source = file, target = backUpFile)

        return Result.Success(Unit)
    }
}