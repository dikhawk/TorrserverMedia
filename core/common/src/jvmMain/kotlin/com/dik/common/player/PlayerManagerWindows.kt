package com.dik.common.player

import com.dik.common.cmd.CommandExecutor
import com.dik.common.player.model.PlatformApp

internal class PlayerManagerWindows(
    private val commandExecutor: CommandExecutor,
) : PlayerManager {

//    powershell -command "Get-ItemProperty 'HKCU:\Software\Microsoft\Windows\CurrentVersion\Explorer\FileExts\.mp4\OpenWithList' | Select-Object -ExpandProperty Property | Where-Object {${'$'}_ -ne 'MRUList'}"

    override suspend fun getAppsList(contentType: String): List<PlatformApp> {
        val cmd = """
            powershell -command 
            "${'$'}ext='.mp4'; ${'$'}p='HKCU:\Software\Microsoft\Windows\CurrentVersion\Explorer\FileExts\${'$'}ext\OpenWithList'; 
            ${'$'}obj=Get-ItemProperty ${'$'}p; ${'$'}obj.Property | ?{${'$'}_ -ne 'MRUList'} | %{ 
            ${'$'}exe=${'$'}obj.${'$'}_; ${'$'}fn=(gp \"HKLM:\SOFTWARE\Classes\Applications\${'$'}exe\" -E SilentlyContinue).FriendlyAppName; 
            if(!${'$'}fn){${'$'}fn='Unknown'}; write-host \"${'$'}exe;${'$'}fn\" }"
        """.trimIndent()
            .replace("\n", " ")

        val appList = commandExecutor.runAndWaitResult(cmd)
        val test = appList
        TODO("Not yet implemented")
    }

    override suspend fun openFile(appId: String, filePath: String) {
        TODO("Not yet implemented")
    }
}