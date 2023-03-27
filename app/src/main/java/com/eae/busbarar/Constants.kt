package com.eae.busbarar

import android.Manifest

object Constants {

    const val REQUEST_CODE_PERMISSION = 123
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    const val BASE_URL = "http://10.45.20.92/"
    const val BASE_URL_WEB_CHART = "http://10.45.20.92/csrender"
    const val BASE_URL_ALERT_SCREEN = "10.45.20.92/rulewebview"
    const val USER_AGENT = "Thunder Client(https://www.thunderclient.com/)"
    const val startDateTime = "2022-11-14 11:15:00"
    const val endDateTime = "2022-11-14 13:30:00"

}