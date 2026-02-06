package com.dik.torrserverapi.domain.usecases

import co.touchlab.kermit.Logger
import com.dik.common.AppDispatchers
import com.dik.common.Result
import com.dik.torrserverapi.TorrserverError
import com.dik.torrserverapi.domain.filemanager.FileManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

internal class RestoreServerFromBackUpUseCase(
    private val dispatchers: AppDispatchers,
    private val fileManager: FileManager,
) {
    private val tag = "RestoreServerFromBackUp:"

    suspend operator fun invoke(
        pathToBackupFile: String,
        pathToFile: String
    ): Result<Unit, TorrserverError> {
        Logger.i("$tag start restore server $pathToFile from backup file $pathToBackupFile")

        return withContext(dispatchers.ioDispatcher()) {
            try {
                if (!fileManager.exists(pathToBackupFile)) {
                    Logger.e("$tag backup file not exist $pathToBackupFile")
                    return@withContext Result.Error(
                        TorrserverError.Server
                            .FileNotExist("Backup file not exist: $pathToBackupFile")
                    )
                }

                if (fileManager.exists(pathToFile)) fileManager.delete(pathToFile)

                fileManager.copy(source = pathToBackupFile, target = pathToFile)

                Logger.i("$tag file restored from backup successfully")

                return@withContext Result.Success(Unit)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                return@withContext Result.Error(TorrserverError.Unknown(e.message ?: e.toString()))
            }
        }
    }
}