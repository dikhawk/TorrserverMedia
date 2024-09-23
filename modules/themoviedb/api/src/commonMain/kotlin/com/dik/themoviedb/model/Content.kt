package com.dik.themoviedb.model

import com.dik.themoviedb.MediaType

abstract class Content {
    abstract val id: Int
    abstract val mediaType: MediaType
}