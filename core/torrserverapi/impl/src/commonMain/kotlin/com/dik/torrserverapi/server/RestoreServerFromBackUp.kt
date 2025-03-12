package com.dik.torrserverapi.server

import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path.Companion.toPath

class RestoreServerFromBackUp(
    private val dispatchers: AppDispatchers,
    private val fileSystem: FileSystem = FileSystem.SYSTEM
) {
    private val tag = "RestoreServerFromBackUp:"

    suspend operator fun invoke(pathToBackupFile: String, pathToFile: String): Result<Unit, TorrserverError> {
        Logger.i("$tag start restore server $pathToFile from backup file $pathToBackupFile")

        return withContext(dispatchers.ioDispatcher()) {
            val file = pathToFile.toPath()
            val backUpFile = pathToBackupFile.toPath()

            if (!fileSystem.exists(backUpFile)) {
                Logger.e("$tag backup file not exist $pathToBackupFile")
                return@withContext Result.Error(TorrserverError.Server.FileNotExist("Backup file not exist: $pathToBackupFile"))
            }

            if (fileSystem.exists(file)) fileSystem.delete(file)

            fileSystem.copy(source = backUpFile, target = file)

            Logger.i("$tag file restored from backup successfully")

            return@withContext Result.Success(Unit)
        }
    }
}