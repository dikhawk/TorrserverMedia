package com.dik.torrentlist.screens.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun MainUi(component: MainComponent, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxSize()) {
        Text("MainUi")
    }
}