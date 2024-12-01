package com.dik.common

import android.app.Activity

interface CurrentActivityProvider {
    fun getActiveActivity(): Activity?
}