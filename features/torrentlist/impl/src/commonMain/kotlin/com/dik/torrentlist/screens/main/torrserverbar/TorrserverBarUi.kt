package com.dik.torrentlist.screens.main.torrserverbar

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun TorrserverBarUi(component: TorrserverBarComponent, modifier: Modifier = Modifier) {
    Column {
        Text("TorrserverBar")
        Button(onClick = { component.onClickInstallServer() }) {
            Text("Install server")
        }
    }
}