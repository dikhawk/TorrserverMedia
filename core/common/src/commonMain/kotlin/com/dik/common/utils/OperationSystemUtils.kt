package com.dik.common.utils

import com.dik.common.Platform

expect fun platformName(): Platform

expect fun Process.readOutput(): String