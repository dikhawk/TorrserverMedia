package com.dik.torrentlist

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.stack.Children
import com.dik.torrentlist.MainTorrentListComponent.Child.Details
import com.dik.torrentlist.MainTorrentListComponent.Child.TorrentList
import com.dik.torrentlist.details.DetailsUi
import com.dik.torrentlist.list.TorrentListUi

@Composable
fun MainTorrentListUi(component: MainTorrentListComponent, modifier: Modifier = Modifier) {

//    Column(modifier = modifier.fillMaxSize()) {
//        TorrentListUi(component.torrentListComponent())
//    }

    Children(
        stack = component.childStack,
        modifier = modifier
    ) { child ->
        when(val instance = child.instance) {
            is Details -> DetailsUi(instance.component)
            is TorrentList -> TorrentListUi(instance.component)
        }
    }
}