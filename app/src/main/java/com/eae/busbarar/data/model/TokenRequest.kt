package com.eae.busbarar.data.model

import android.net.wifi.WifiManager
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class TokenRequest(
    val token: String?,
    val macAddress: String?
): Parcelable