package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TorrserverBarUi(component: TorrserverBarComponent, modifier: Modifier = Modifier) {
    val uiState = component.uiState.collectAsState()

    Column {
        Text("TorrserverBar")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Button(
                onClick = { component.onClickInstallServer() },
                enabled = !uiState.value.isShowProgress
            ) {
                Text("Install server")
            }
            Spacer(modifier = Modifier.padding(8.dp))
            Text(uiState.value.serverStatus)
            Button(
                onClick = { component.onClickRestartServer() },
                enabled = !uiState.value.isShowProgress
            ) {
                Text("Start server")
            }

            Spacer(modifier = Modifier.padding(8.dp))

            Button(
                onClick = { component.onStopServer() },
                enabled = !uiState.value.isShowProgress
            ) {
                Text("Stop server")
            }
        }

        if (uiState.value.isShowProgress) {
            Spacer(modifier = Modifier.padding(8.dp))
            LinearProgressIndicator(
                progress = { uiState.value.progressUpdate.toFloat() },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}