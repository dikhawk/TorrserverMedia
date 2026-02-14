package com.dik.common.player

import com.dik.common.player.model.PlatformApp

interface PlayerManager {

    /**
     * Retrieves a list of applications that can handle the given content type.
     *
     * @param contentType On Linux and Android, this should be a MIME type (e.g., "video/mp4").
     * On Windows, this should be a file extension (e.g., ".mp4").
     * @return A list of [PlatformApp] that can open the given content type.
     */
    suspend fun getAppsList(contentType: String): List<PlatformApp>
    suspend fun openFile(appId: String, filePath: String)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect object PlayerManagerFactory
