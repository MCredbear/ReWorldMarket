package dev.krysztal.rwm.extender

import dev.krysztal.rwm.ReWorldMarketMain

fun String.getStringConfig() = ReWorldMarketMain.INSTANCE.config.getString(this)
