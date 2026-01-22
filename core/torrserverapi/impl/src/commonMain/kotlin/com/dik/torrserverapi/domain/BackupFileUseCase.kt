package com.dik.torrserverapi.domain

import co.touchlab.kermit.Logger
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import okio.FileSystem
import okio.Path.Companion.toPath

internal class BackupFileUseCase(
    private val fileSystem: FileSystem = FileSystem.Companion.SYSTEM
) {
    private val tag = "BackupFile:"

    operator fun invoke(
        pathToFile: String,
        pathToBackupFile: String
    ): Result<Unit, TorrserverError> {
        val file = pathToFile.toPath()
        val backUpFile = pathToBackupFile.toPath()
        Logger.Companion.i("$tag started backup for file: $pathToFile")

        if (!fileSystem.exists(file)) {
            Logger.Companion.e("$tag File not exist: $pathToFile")
            return Result.Error(TorrserverError.Server.FileNotExist("File not exist: $pathToFile"))
        }

        try {
            fileSystem.delete(backUpFile)
            fileSystem.copy(source = file, target = backUpFile)
        } catch (e: Exception) {
            Logger.Companion.e("$tag Error $e")
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }

        Logger.Companion.i("$tag Success result. Created backup for file: $pathToFile,  saved to $pathToBackupFile")
        return Result.Success(Unit)
    }
}