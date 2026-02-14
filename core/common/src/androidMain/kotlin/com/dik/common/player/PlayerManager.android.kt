package com.dik.common.player

import android.content.Context

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual object PlayerManagerFactory {

    fun instance(context: Context): PlayerManager {
        return PlayerManagerAndroid(context)
    }
}