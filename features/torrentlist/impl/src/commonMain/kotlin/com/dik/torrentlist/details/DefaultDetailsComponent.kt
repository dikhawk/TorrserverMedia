package com.dik.torrentlist.details

import com.arkivanov.decompose.ComponentContext

class DefaultDetailsComponent(
    componentContext: ComponentContext
) : ComponentContext by componentContext, DetailsComponent {

}