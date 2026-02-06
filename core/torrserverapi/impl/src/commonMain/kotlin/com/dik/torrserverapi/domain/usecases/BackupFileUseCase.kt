package com.dik.torrserverapi.domain.usecases

import co.touchlab.kermit.Logger
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.filemanager.FileManager

internal class BackupFileUseCase(
    private val fileManager: FileManager,
) {
    private val tag = "BackupFile:"

    suspend operator fun invoke(
        pathToFile: String,
        pathToBackupFile: String
    ): Result<Unit, TorrserverError> {
        Logger.i("$tag started backup for file: $pathToFile")

        if (!fileManager.exists(pathToFile)) {
            Logger.e("$tag File not exist: $pathToFile")
            return Result.Error(TorrserverError.Server.FileNotExist("File not exist: $pathToFile"))
        }

        try {
            fileManager.delete(pathToBackupFile)
            fileManager.copy(source = pathToFile, target = pathToBackupFile)
        } catch (e: Exception) {
            Logger.e("$tag Error $e")
            return Result.Error(TorrserverError.Unknown(e.toString()))
        }

        Logger.i("$tag Success result. Created backup for file: $pathToFile,  saved to $pathToBackupFile")
        return Result.Success(Unit)
    }
}